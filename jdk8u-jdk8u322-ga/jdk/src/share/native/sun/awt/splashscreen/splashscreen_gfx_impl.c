/*
 * Copyright (c) 2005, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

#include "splashscreen_gfx_impl.h"

/* *INDENT-OFF* */
const byte_t baseDitherMatrix[DITHER_SIZE][DITHER_SIZE] = {
  /* Bayer's order-4 dither array.  Generated by the code given in
   * Stephen Hawley's article "Ordered Dithering" in Graphics Gems I.
   */
  {   0,192, 48,240, 12,204, 60,252,  3,195, 51,243, 15,207, 63,255 },
  { 128, 64,176,112,140, 76,188,124,131, 67,179,115,143, 79,191,127 },
  {  32,224, 16,208, 44,236, 28,220, 35,227, 19,211, 47,239, 31,223 },
  { 160, 96,144, 80,172,108,156, 92,163, 99,147, 83,175,111,159, 95 },
  {   8,200, 56,248,  4,196, 52,244, 11,203, 59,251,  7,199, 55,247 },
  { 136, 72,184,120,132, 68,180,116,139, 75,187,123,135, 71,183,119 },
  {  40,232, 24,216, 36,228, 20,212, 43,235, 27,219, 39,231, 23,215 },
  { 168,104,152, 88,164,100,148, 84,171,107,155, 91,167,103,151, 87 },
  {   2,194, 50,242, 14,206, 62,254,  1,193, 49,241, 13,205, 61,253 },
  { 130, 66,178,114,142, 78,190,126,129, 65,177,113,141, 77,189,125 },
  {  34,226, 18,210, 46,238, 30,222, 33,225, 17,209, 45,237, 29,221 },
  { 162, 98,146, 82,174,110,158, 94,161, 97,145, 81,173,109,157, 93 },
  {  10,202, 58,250,  6,198, 54,246,  9,201, 57,249,  5,197, 53,245 },
  { 138, 74,186,122,134, 70,182,118,137, 73,185,121,133, 69,181,117 },
  {  42,234, 26,218, 38,230, 22,214, 41,233, 25,217, 37,229, 21,213 },
  { 170,106,154, 90,166,102,150, 86,169,105,153, 89,165,101,149, 85 }
};
/* *INDENT-ON* */

// FIXME: tinting on some colormaps (e.g. 1-2-1) means something is slightly wrong with
// colormap calculation... probably it's some rounding error

/*  calculates the colorTable for mapping from 0..255 to 0..numColors-1
    also calculates the dithering matrix, scaling baseDitherMatrix accordingly */
void
initDither(DitherSettings * pDither, int numColors, int scale)
{
    int i, j;

    pDither->numColors = numColors;
    for (i = 0; i < (MAX_COLOR_VALUE + 1) * 2; i++) {
        pDither->colorTable[i] =
            (((i > MAX_COLOR_VALUE) ? MAX_COLOR_VALUE : i) *
             (numColors - 1) / MAX_COLOR_VALUE) * scale;
    }
    for (i = 0; i < DITHER_SIZE; i++)
        for (j = 0; j < DITHER_SIZE; j++)
            pDither->matrix[i][j] =
                (int) baseDitherMatrix[i][j] / (numColors - 1);
}

/* scale a number on the range of 0..numColorsIn-1 to 0..numColorsOut-1
 0 maps to 0 and numColorsIn-1 maps to numColorsOut-1
 intermediate values are spread evenly between 0 and numColorsOut-1 */
INLINE int
scaleColor(int color, int numColorsIn, int numColorsOut)
{
    return (color * (numColorsOut - 1) + (numColorsIn - 1) / 2)
        / (numColorsIn - 1);
}

/*  build a colormap for a color cube and a dithering matrix. color cube is quantized
    according to the provided maximum number of colors */
int
quantizeColors(int maxNumColors, int *numColors)
{

    // static const int scale[3]={10000/11,10000/69,10000/30};
    // FIXME: sort out the adaptive color cube subdivision... realistic 11:69:30 is good on photos,
    // but would be bad on other pictures. A stupid approximation is used now.

    static const int scale[3] = { 8, 4, 6 };

    // maxNumColors should be at least 2x2x2=8, or we lose some color components completely
    numColors[0] = numColors[1] = numColors[2] = 2;

    while (1) {
        int idx[3] = { 0, 1, 2 };
        /* bubble sort the three indexes according to scaled numColors values */
#define SORT(i,j) \
        if (numColors[idx[i]]*scale[idx[i]]>numColors[idx[j]]*scale[idx[j]]) \
            { int t = idx[i]; idx[i] = idx[j]; idx[j] = t; }
        SORT(0, 1);
        SORT(1, 2);
        SORT(0, 1);
        /* try increasing numColors for the first color */
        if ((numColors[idx[0]] + 1) * numColors[idx[1]] *
            numColors[idx[2]] <= maxNumColors) {
                numColors[idx[0]]++;
        } else if (numColors[idx[0]] * (numColors[idx[1]] + 1) *
            numColors[idx[2]] <= maxNumColors) {
            numColors[idx[1]]++;
        } else if (numColors[idx[0]] * numColors[idx[1]] *
            (numColors[idx[2]] + 1) <= maxNumColors) {
            numColors[idx[2]]++;
        } else {
            break;
        }
    }
    return numColors[0] * numColors[1] * numColors[2];
}

void
initColorCube(int *numColors, rgbquad_t * pColorMap, DitherSettings * pDithers,
              rgbquad_t * colorIndex)
{
    int r, g, b, n;

    n = 0;
    for (r = 0; r < numColors[2]; r++) {
        for (g = 0; g < numColors[1]; g++)
            for (b = 0; b < numColors[0]; b++) {
                pColorMap[colorIndex[n++]] =
                    scaleColor(b, numColors[0], MAX_COLOR_VALUE) +
                    (scaleColor(g, numColors[1], MAX_COLOR_VALUE) << 8) +
                    (scaleColor(r, numColors[2], MAX_COLOR_VALUE) << 16);
            }
    }
    initDither(pDithers + 0, numColors[0], 1);
    initDither(pDithers + 1, numColors[1], numColors[0]);
    initDither(pDithers + 2, numColors[2], numColors[1] * numColors[0]);
}

/*
    the function below is a line conversion loop

    incSrc and incDst are pSrc and pDst increment values for the loop, in bytes
    mode defines how the pixels should be processed

    mode==CVT_COPY means the pixels should be copied as is
    mode==CVT_ALPHATEST means pixels should be skipped when source pixel alpha is above the threshold
    mode==CVT_BLEND means alpha blending between source and destination should be performed, while
    destination alpha should be retained. source alpha is used for blending.
*/
void
convertLine(void *pSrc, int incSrc, void *pDst, int incDst, int numSamples,
            ImageFormat * srcFormat, ImageFormat * dstFormat, int doAlpha,
            void *pSrc2, int incSrc2, ImageFormat * srcFormat2,
            int row, int col)
{
    int i;

    switch (doAlpha) {
    case CVT_COPY:
        for (i = 0; i < numSamples; ++i) {
            putRGBADither(getRGBA(pSrc, srcFormat), pDst, dstFormat,
                row, col++);
            INCPN(byte_t, pSrc, incSrc);
            INCPN(byte_t, pDst, incDst);
        }
        break;
    case CVT_ALPHATEST:
        for (i = 0; i < numSamples; ++i) {
            rgbquad_t color = getRGBA(pSrc, srcFormat);

            if (color >= ALPHA_THRESHOLD) {     // test for alpha component >50%. that's an extra branch, and it's bad...
                putRGBADither(color, pDst, dstFormat, row, col++);
            }
            INCPN(byte_t, pSrc, incSrc);
            INCPN(byte_t, pDst, incDst);
        }
        break;
    case CVT_BLEND:
        for (i = 0; i < numSamples; ++i) {
            rgbquad_t src = getRGBA(pSrc, srcFormat);
            rgbquad_t src2 = getRGBA(pSrc2, srcFormat);

            putRGBADither(blendRGB(src, src2,
                QUAD_ALPHA(src2)) | (src & QUAD_ALPHA_MASK), pDst, dstFormat,
                row, col++);
            INCPN(byte_t, pSrc, incSrc);
            INCPN(byte_t, pDst, incDst);
            INCPN(byte_t, pSrc2, incSrc2);
        }
        break;
    }
}

/* initialize ImageRect structure according to function arguments */
void
initRect(ImageRect * pRect, int x, int y, int width, int height, int jump,
         int stride, void *pBits, ImageFormat * format)
{
    int depthBytes = format->depthBytes;

    pRect->pBits = pBits;
    INCPN(byte_t, pRect->pBits, (intptr_t) y * stride + x * depthBytes);
    pRect->numLines = height;
    pRect->numSamples = width;
    pRect->stride = stride * jump;
    pRect->depthBytes = depthBytes;
    pRect->format = format;
    pRect->row = y;
    pRect->col = x;
    pRect->jump = jump;
}

/*  copy image rectangle from source to destination, or from two sources with blending */

int
convertRect(ImageRect * pSrcRect, ImageRect * pDstRect, int mode)
{
    return convertRect2(pSrcRect, pDstRect, mode, NULL);
}

int
convertRect2(ImageRect * pSrcRect, ImageRect * pDstRect, int mode,
             ImageRect * pSrcRect2)
{
    int numLines = pSrcRect->numLines;
    int numSamples = pSrcRect->numSamples;
    void *pSrc = pSrcRect->pBits;
    void *pDst = pDstRect->pBits;
    void *pSrc2 = NULL;
    int j, row;

    if (pDstRect->numLines < numLines)
        numLines = pDstRect->numLines;
    if (pDstRect->numSamples < numSamples) {
        numSamples = pDstRect->numSamples;
    }
    if (pSrcRect2) {
        if (pSrcRect2->numLines < numLines) {
            numLines = pSrcRect2->numLines;
        }
        if (pSrcRect2->numSamples < numSamples) {
            numSamples = pSrcRect2->numSamples;
        }
        pSrc2 = pSrcRect2->pBits;
    }
    row = pDstRect->row;
    for (j = 0; j < numLines; j++) {
        convertLine(pSrc, pSrcRect->depthBytes, pDst, pDstRect->depthBytes,
            numSamples, pSrcRect->format, pDstRect->format, mode,
            pSrc2, pSrcRect2 ? pSrcRect2->depthBytes : 0,
            pSrcRect2 ? pSrcRect2->format : 0, row, pDstRect->col);
        INCPN(byte_t, pSrc, pSrcRect->stride);
        INCPN(byte_t, pDst, pDstRect->stride);
        if (pSrcRect2) {
            INCPN(byte_t, pSrc2, pSrcRect2->stride);
        }
        row += pDstRect->jump;
    }
    return numLines * pSrcRect->stride;
}

int
fillRect(rgbquad_t color, ImageRect * pDstRect)
{
    int numLines = pDstRect->numLines;
    int numSamples = pDstRect->numSamples;
    void *pDst = pDstRect->pBits;
    int j, row;

    row = pDstRect->row;
    for (j = 0; j < numLines; j++) {
        fillLine(color, pDst, pDstRect->depthBytes, numSamples,
            pDstRect->format, row, pDstRect->col);
        INCPN(byte_t, pDst, pDstRect->stride);
        row += pDstRect->jump;
    }
    return numLines * pDstRect->stride;
}

/* init the masks; all other parameters are initialized to default values */
void
initFormat(ImageFormat * format, int redMask, int greenMask, int blueMask,
           int alphaMask)
{
    int i, shift, numBits;

    format->byteOrder = BYTE_ORDER_NATIVE;
    format->colorMap = NULL;
    format->depthBytes = 4;
    format->fixedBits = 0;
    format->premultiplied = 0;
    format->mask[0] = blueMask;
    format->mask[1] = greenMask;
    format->mask[2] = redMask;
    format->mask[3] = alphaMask;
    for (i = 0; i < 4; i++) {
        getMaskShift(format->mask[i], &shift, &numBits);
        format->shift[i] = shift + numBits - i * 8 - 8;
    }
}

/* dump the visual format */
void
dumpFormat(ImageFormat * format)
{
#ifdef _DEBUG
    int i;

    printf("byteorder=%d colormap=%08x depthBytes=%d fixedBits=%08x transparentColor=%u ",
        format->byteOrder, (unsigned) format->colorMap, format->depthBytes,
        (unsigned) format->fixedBits, (unsigned) format->transparentColor);
    for (i = 0; i < 4; i++) {
        printf("mask[%d]=%08x shift[%d]=%d\n", i, (unsigned) format->mask[i], i,
            format->shift[i]);
    }
    printf("\n");
#endif
}

/* optimize the format */
void
optimizeFormat(ImageFormat * format)
{
    if (platformByteOrder() == format->byteOrder && format->depthBytes != 3) {
        format->byteOrder = BYTE_ORDER_NATIVE;
    }
    /* FIXME: some advanced optimizations are possible, especially for format pairs */
}

int
platformByteOrder()
{
    int test = 1;

    *(char *) &test = 0;
    return test ? BYTE_ORDER_MSBFIRST : BYTE_ORDER_LSBFIRST;
}