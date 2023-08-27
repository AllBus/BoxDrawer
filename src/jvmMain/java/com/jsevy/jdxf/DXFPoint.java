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
 * Class representing a simple point in a diagram.
 * 
 * @author jsevy
 *
 */
public class DXFPoint extends DXFGraphicalEntity
{
    private RealPoint point;
    
    
    /**
     * Create a point. The graphical representation of the point depends on the supplied Paint object, which specifies
     * the size of the point through the line thickness.
     * 
     * @param point		Location of the point
     * @param graphics  The graphics object specifying parameters for this entity (color, thickness)
     */
    public DXFPoint(RealPoint point, DXFGraphics graphics)
    {
        // assign layer and color/line properties
        super(graphics);
        
        this.point = new RealPoint(point);
    }
    
    
    
    /**
     * Implementation of DXFObject interface method; creates DXF text representing the point.
     */
    public String toDXFString()
    {
        StringBuilder result = new StringBuilder("0\nPOINT\n");
        
        // print out handle and superclass marker(s)
        result.append(super.toDXFString());
        
        // print out subclass marker
        result.append("100\nAcDbPoint\n");
        
        result.append("10\n" + setPrecision(point.x) + "\n");
        result.append("20\n" + setPrecision(point.y) + "\n");
        result.append("30\n" + setPrecision(point.z) + "\n");
               
        return result.toString();
    }
}