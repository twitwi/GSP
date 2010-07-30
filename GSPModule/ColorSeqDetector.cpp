#include "ColorSeqDetector.hpp"
#include <cstdio>

ColorSeqDetector::ColorSeqDetector()
  : img(0)
  , stateImg(0)
  , period(5)
{
}

void ColorSeqDetector::initModule()
{
}

void ColorSeqDetector::stopModule()
{
  if(img)
    cvReleaseImage(&img);
  if(stateImg)
    cvReleaseImage(&stateImg);
}

bool ColorSeqDetector::isRed(unsigned char h, unsigned char s, unsigned char v)
{
  return (h>=150 || h<10);
}

bool ColorSeqDetector::isGreen(unsigned char h, unsigned char s, unsigned char v)
{
  return (h>=70 && h<95);
}

bool ColorSeqDetector::isBlue(unsigned char h, unsigned char s, unsigned char v)
{
  return (h>=95 && h<125);
}

bool ColorSeqDetector::isYellow(unsigned char h, unsigned char s, unsigned char v)
{
  return (h>=10 && h<80);
}

void ColorSeqDetector::setPeriod( int p)
{
  period = p;
}

void ColorSeqDetector::input(IplImage *inputImg)
{
  if(!img)
  {
    img = cvCreateImage(cvGetSize(inputImg), IPL_DEPTH_32F, 1);
    cvZero(img);
  }
  if(!stateImg)
  {
    stateImg = cvCreateImage(cvGetSize(inputImg), IPL_DEPTH_8U, 3);
    cvZero(stateImg);
  }
  
  else if(img->width != inputImg->width
          || img->height != inputImg->height)
  {
    cvReleaseImage(&img);
    img = cvCreateImage(cvGetSize(inputImg), IPL_DEPTH_32F, 1);
    cvReleaseImage(&stateImg);
    stateImg = cvCreateImage(cvGetSize(inputImg), IPL_DEPTH_8U, 3);
    cvZero(stateImg);
    cvZero(img);
  }

  uchar *data;
  int step;
  CvSize(size);
  uchar *state_data;
  int state_step;
  float *float_data;
  int float_step;

  cvGetRawData(inputImg, (uchar**)&data, &step, &size);
  step /= sizeof(data[0]);
  cvGetRawData(img, (uchar**)&float_data, &float_step, &size);
  float_step /= sizeof(float_data[0]);
  cvGetRawData(stateImg, (uchar**)&state_data, &state_step, &size);
  state_step /= sizeof(state_data[0]);
  
  for(int y = 0; y < size.height;
      y++, data += step, state_data+=state_step, float_data+=float_step )
    for(int x = 0; x < size.width; x++ )
    {
      uchar h = data[3*x];
      uchar s = data[3*x+1];
      uchar v = data[3*x+2];
      uchar state = state_data[3*x];
      uchar sstate = state_data[3*x+1];
      uchar bstate = state_data[3*x+1];
      float score = float_data[x];
      
      switch(state){
      case 0: // invalid state
        if(isRed(h,s,v))
        {
          state = 1;
          sstate = 0;
          bstate = 0;
        }
        break;
      case 1: // red state
        if(isRed(h,s,v))
        {
          sstate++;
          if(sstate > period+2)
          {
            sstate = 0;
            state = 0;
          }
        }else if(sstate >= period-2 && isGreen(h,s,v))
        {
          state = 2;
          sstate = 0;
          bstate = 0;
        }else{
          bstate ++;
          if(bstate > 1)
          {
            state = 0;
            sstate = 0;
          }
        }
        break;
      case 2: // green state
        if(isGreen(h,s,v))
        {
          sstate++;
          if(sstate > period+2)
          {
            sstate = 0;
            state = 0;
          }
        }else if(sstate >= period - 2 && isBlue(h,s,v))
        {
          state = 3;
          sstate = 0;
          bstate = 0;
        }else{
          bstate ++;
          if(bstate > 1)
          {
            state = 0;
            sstate = 0;
          }
        }
        break;
      case 3: // blue state
        if(isBlue(h,s,v))
        {
          sstate++;
          if(sstate > period+2)
          {
            sstate = 0;
            state = 0;
          }
        }else if(sstate >= period-2 && isYellow(h,s,v))
        {
          state = 4;
          sstate = 0;
          bstate = 0;
        }else{
          bstate ++;
          if(bstate > 1)
          {
            state = 0;
            sstate = 0;
          }
        }
        break;
      case 4: // yellow state
        if(isYellow(h,s,v))
        {
          sstate++;
          if(sstate > period+2)
          {
            sstate = 0;
            state = 0;
          }
        }else if(sstate >= period-2 && isRed(h,s,v))
        {
          state = 1;
          sstate = 0;
          bstate = 0;

          score = 1.;
          //float_data[x]=1.;
        }else{
          bstate ++;
          if(bstate > 1)
          {
            state = 0;
            sstate = 0;
          }
        }
        break;
      default:
        state = 0;
        sstate = 0;
        break;
      }
      
      state_data[3*x] = state;
      state_data[3*x+1] = sstate;
      //float_data[x] = (float) state / 4.f;

      score -= 0.005;
      if(score < 0. )
      {
        score = 0.;
      }
      float_data[x] = score;
      
//       if(state == 0){
//         float_data[x] = 0.;
//       }
    }
  
  emitNamedEvent("output", img);
}

