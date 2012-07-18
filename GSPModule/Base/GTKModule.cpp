#include <cassert>

#include <gtk/gtk.h>

#include "GTKModule.hpp"

static gboolean refreshWindowImageWrapper(gpointer data)
{
    GTKModule *module = (GTKModule *)data;

    module->refreshWindowImage();

    return TRUE;
}

static gpointer gtkMainThreadFunction(gpointer data)
{
    g_timeout_add(100, (GSourceFunc)refreshWindowImageWrapper, (gpointer)data);

    gdk_threads_enter();
    gtk_main();
    gdk_threads_leave(); 
}

GTKModule::GTKModule(void)
{
    if (!g_thread_supported()) {
        g_thread_init(NULL);
    }
    gdk_threads_init();
    gtk_init(NULL, NULL);
    
    gdk_threads_enter();

    this->mutex = g_mutex_new();

    this->window = gtk_window_new(GTK_WINDOW_TOPLEVEL);
    assert(this->window != NULL);
    gtk_window_set_resizable((GtkWindow *)this->window, FALSE);

    //g_signal_connect(window, "destroy", G_CALLBACK(gtk_main_quit), NULL);

    this->image = gtk_image_new();
    assert(this->image != NULL);
    gtk_container_add(GTK_CONTAINER(this->window), this->image);

    gtk_widget_show_all(window);

    gdk_threads_leave(); 
}

void GTKModule::initModule(void)
{
    this->gtkmainThread = 
        g_thread_create(gtkMainThreadFunction, this, TRUE, NULL);
    assert(this->gtkmainThread != NULL);
}

void GTKModule::stopModule(void)
{
    g_thread_join(this->gtkmainThread);
    g_mutex_unlock(this->mutex);
}

void GTKModule::setName(const char *name)
{
    gtk_window_set_title((GtkWindow *)this->window, name);
}

static void windowImageUnrefHandler(guchar *pixels, gpointer data)
{
    delete [] pixels;
}

void GTKModule::inputRGB(const unsigned char *rgb, int width, int height)
{
    if (g_mutex_trylock(this->mutex)) {
        unsigned char *rgbcpy = new unsigned char[width*height*3];
        assert(rgbcpy != NULL);
        memcpy(rgbcpy, rgb, width*height*3*sizeof(unsigned char));

        gdk_threads_enter();

        GdkPixbuf *pixbuf =
            gdk_pixbuf_new_from_data(
                    (const guchar *)rgbcpy,
                    GDK_COLORSPACE_RGB,
                    FALSE,
                    8,
                    width,
                    height,
                    width*3*sizeof(unsigned char),
                    windowImageUnrefHandler,
                    NULL);
        assert(pixbuf != NULL);
        gtk_image_set_from_pixbuf((GtkImage *)this->image, pixbuf);
        g_object_unref(pixbuf);

        gtk_window_resize((GtkWindow *)this->window, width, height);

        gdk_threads_leave(); 
    }
}

void GTKModule::input(const IplImage *ipl)
{
    IplImage *rgbIpl = NULL;

    /* For now, only accept unsigned 8bits depth images */
    assert(ipl->depth == IPL_DEPTH_8U);

    /* We can't know the input colorspace... Take the assumption it is BGR, the
     * default loaded by OpenCV -- and so, our image modules -- or grayscale */
    assert(ipl->nChannels == 1 || ipl->nChannels == 3);
    rgbIpl = cvCreateImage(cvSize(ipl->width, ipl->height), IPL_DEPTH_8U, 3);
    assert(rgbIpl != NULL);
    if (ipl->nChannels == 1) {
        cvCvtColor(ipl, rgbIpl, CV_GRAY2RGB);
    }
    else if (ipl->nChannels == 3) {
        cvCvtColor(ipl, rgbIpl, CV_BGR2RGB);
    }

    inputRGB((unsigned char *)rgbIpl->imageData, ipl->width, ipl->height);

    cvReleaseImage(&rgbIpl);
}

void GTKModule::refreshWindowImage(void)
{
    g_mutex_unlock(this->mutex);
}

