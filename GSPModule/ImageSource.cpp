
#include "ImageSource.hpp"

#include <opencv/highgui.h>
#include <unistd.h>
#include <stdio.h>

using namespace std;

static void freeImage(IplImage* &image) {
    if (image) {
        cvReleaseImage(&image);
        image = NULL;
    }
}
static bool startsWith(string &io, const char* prefix) {
    if (strncmp(io.c_str(), prefix, strlen(prefix))) {
        return false;
    } else {
        io = string(io.c_str() + strlen(prefix));
        return true;
    }
}


ImageSource::ImageSource() : currentImage(NULL) {}

void ImageSource::input() {
    switch (mode) {
    case 0: {
        freeImage(currentImage);
        char buf[256];
        snprintf(buf, 255, url.c_str(), imageIndex);
        currentImage = cvLoadImage(buf);
        if (!currentImage) {
            mode = -2;
            return;
        }
        emitNamedEvent("output", currentImage);
        imageIndex++;
    }
        break;
    case 1:
        break;
    }
}

void ImageSource::setUrl(char *url) {
    if (this->url != "") {
        throw "'url' already set";
    }
    this->url = url;
}

void ImageSource::initModule() {
    if (url == "") {
        throw "'url' is unset";
    }
    mode = -1;
    if (startsWith(url, "images:")) {
        mode = 0;
        imageIndex = 0;
    } else if (startsWith(url, "video:")) {
        mode = 1;
    }
}

void ImageSource::stopModule()
{
    freeImage(currentImage);
}


