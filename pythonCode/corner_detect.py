# Imports
import numpy as np
import cv2
import math
from pathlib import Path

def blurImage(origImage):
    return cv2.GaussianBlur(origImage,(3,3), sigmaX=0, sigmaY=0)

def convToGray(origImage):
    return cv2.cvtColor(origImage, cv2.COLOR_BGR2GRAY)

def calcCanny(grayImage, lowThreshold = 50 , highThreshold = 150):
    return cv2.Canny(grayImage, lowThreshold, highThreshold)
   
def calcHoughlines(cannyModell, rho = 1, theta = np.pi / 180, threshold = 15, minLineLength = 200, maxLineGap = 7):
    return cv2.HoughLinesP(cannyModell, rho, theta, threshold, np.array([]), minLineLength, maxLineGap)

def slope(x1, y1, x2, y2):
    if not (x2-x1 == 0):
        return (y2 - y1) / (x2 - x1)
    if (x2-x1 == 0):
        return math.inf

def get_diagonal_lines(line_cor):
    vertical = []
    horizontal = []
    diagonals = []
    for line in line_cor:
        for xa, ya, xb, yb in line:
            if slope(xa, ya, xb, yb) == 0:
                horizontal.append(line)
            elif slope(xa, ya, xb, yb) == math.inf:
                vertical.append(line)
            else:
                diagonals.append(line)
    return diagonals

def return_diagonal_lines(origImage: Path):
    return get_diagonal_lines(calcHoughlines(calcCanny(convToGray(blurImage(str(origImage))))))

# This function is overloaded and no longer used
def getHeight(vertical, horizontal):
    topLine = np.full([1,4], None)
    bottomLine = np.full([1,4], None)
    for line in horizontal:
        if topLine[0][1] is None and bottomLine[0][1] is None:
            bottomLine = line
            topLine = line
        elif line[0][1] < topLine[0][1]:
            topLine = line
        elif line[0][1] > bottomLine[0][1]:
            bottomLine = line

    leftLine = np.full([1, 4], None)
    rightLine = np.full([1, 4], None)
    for line in vertical:
        if leftLine[0][0] is None and rightLine[0][0] is None:
            leftLine = line
            rightLine = line
        elif line[0][0] < leftLine[0][0]:
            leftLine = line
        elif line[0][0] > rightLine[0][0]:
            rightLine = line
    return topLine[0][1], bottomLine[0][1], leftLine[0][0], rightLine[0][0]

