/*
 * JDXF Library
 * 
 *   Copyright (C) 2018, Jonathan Sevy <jsevy@jsevy.com>
 *   
 *   Permission is hereby granted, free of charge, to any person obtaining a copy
 *   of this software and associated documentation files (the "Software"), to deal
 *   in the Software without restriction, including without limitation the rights
 *   to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *   copies of the Software, and to permit persons to whom the Software is
 *   furnished to do so, subject to the following conditions:
 *   
 *   The above copyright notice and this permission notice shall be included in all
 *   copies or substantial portions of the Software.
 *   
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *   IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *   FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *   AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *   LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *   OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *   SOFTWARE.
 * 
 */

package com.jsevy.jdxf;



/**
 * Interface for all DXF database objects, including graphical entities. Each
 * subclass will define a method to generate its corresponding DXF text for use in a DXF file,
 * including the database subclass marker, and should call this superclass method to insert the database handle.
 * @author jsevy
 *
 */
public class DXFDatabaseObject implements DXFObject
{
    
    public static final int LINEWEIGHT_BYBLOCK = -2;
    public static final int LINEWEIGHT_BYLAYER = -1;

    
    private static int handleCount = 1;
    
    protected int handle;
    
    public DXFDatabaseObject()
    {
        // assign handle and increment handle count so will be unique
        handle = handleCount;
        handleCount++;
    }
    
    /**
     * Implement DXFObject method; just print out group code and value of handle.
     */
    public String toDXFString()
    {
        // print out handle
        String result = "5\n" + Integer.toHexString(handle) + "\n";
        
        return result;
    }
    
    
    /**
     * Return handle for this object
     * @return Handle for object
     */
    public int getHandle()
    {
        return handle;
    }
    
    
    /**
     * Return current handle count so can know what ones have been used
     * @return Current handle count
     */
    public static int getHandleCount()
    {
        return handleCount;
    }
    
    
    /**
     * Set current handle count; used only during initialization in DXFDocument
     * @param count  Handle count
     */
    protected static void setHandleCount(int count)
    {
        handleCount = count;
    }
    
    
    /**
     * Utility to determine linewidth for DXF output
     * 
     * @param   linewidth    Java line width specified in pixels at 72 pixels/inch
     * @return  DXF line width specified in 1/100 of mm, with restricted range
     */
    public int getDXFLineWeight(double linewidth)
    {
        // Java line width specified in pixels at 72 pixels/inch; DXF line width specified in 1/100 of mm, with restricted range
        // Range: 0, 5, 9, 13, 15, 18, 20, 25, 30, 35, 40, 50, 53, 60, 70, 80, 90, 100, 106, 120, 140, 158, 200, 211, -1 (by layer), -2 (by block), -3 (default)
        // so scale by (1 in/72 pixels) * (25.4 mm / 1 in) * 100, and map to nearest available weight
        double scale = 25.4 * 100 / 72;
        double lineWidthMMHundredths = linewidth * scale;
        int[] lineWidthOptions = {0, 5, 9, 13, 15, 18, 20, 25, 30, 35, 40, 50, 53, 60, 70, 80, 90, 100, 106, 120, 140, 158, 200, 211};
        
        int i;
        for (i = 1; i < lineWidthOptions.length; i++)
        {
            if (lineWidthOptions[i] - lineWidthMMHundredths > 0)
                break;
        }
        lineWidthMMHundredths = lineWidthOptions[i-1];
        
        return (int)lineWidthMMHundredths;
    }
    
    
}