
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
static bool startsWithAndRemove(string &io, const char* prefix) {
    if (strncmp(io.c_str(), prefix, strlen(prefix))) {
        return false;
    } else {
        io = string(io.c_str() + strlen(prefix));
        return true;
    }
}


ImageSource::ImageSource() : currentImage(NULL), imageIndex(-1) {}

void ImageSource::skip() {
    switch (mode) {
    case 0:
        imageIndex++;
        break;
    }
}
void ImageSource::input() {
    switch (mode) {
    case 0: {
        freeImage(currentImage);
        char buf[256];
        snprintf(buf, 255, url.c_str(), imageIndex);
        if (gray) {
            currentImage = cvLoadImage(buf, 0);
        } else {
            currentImage = cvLoadImage(buf);
        }
        if (!currentImage) {
            mode = -2;
            fprintf(stderr, "Could not grab image '%s'\n", buf);
            return;
        }
        fprintf(stderr, "grabbed image '%s'\n", buf);
        emitNamedEvent("output", currentImage);
        imageIndex++;
    }
        break;
    case 1:
        break;
    }
}

void ImageSource::setStart(int imageIndex) {
    this->imageIndex = imageIndex;
}

void ImageSource::setGray(bool gray) {
    this->gray = gray;
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
    if (startsWithAndRemove(url, "images:")) {
        mode = 0;
        if (imageIndex == -1) imageIndex = 0;
    } else if (startsWithAndRemove(url, "video:")) {
        mode = 1;
    }
}

void ImageSource::stopModule()
{
    freeImage(currentImage);
}


