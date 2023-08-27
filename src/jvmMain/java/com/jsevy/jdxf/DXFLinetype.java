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

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.Arrays;

/**
 * Class representing a linetype for use in LTYPE table.
 * @author jsevy
 *
 */
public class DXFLinetype extends DXFTableRecord
{
    public static final String LINETYPENAME_BYBLOCK = "ByBlock";
    public static final String LINETYPENAME_BYLAYER = "ByLayer";
    
    private String name;
    
    private double[] dashArray;
    
    // used for generating unique linetype names as needed
    private static int linetypeCount = 1;
    
    
    /**
     * Set current linetype count; used only during initialization in DXFDocument
     * 
     * @param count  Linetype count
     */
    protected static void setLinetypeCount(int count)
    {
        linetypeCount = count;
    }
    
    
    /**
     * Create a new linetype with specified name and dash pattern
     * 
     * @param name          Name of this line type table record
     * @param graphics      Graphics instance, used to get BasicStroke indicating pattern and sizes of spaces, dots and dashes
     */
    public DXFLinetype(String name, Graphics2D graphics)
    {
        this.name = name;
        
        BasicStroke stroke = (BasicStroke)graphics.getStroke();
        float[] strokeArray = stroke.getDashArray();
        
        if (strokeArray == null)
        {
            dashArray = null;
        }
        else
        {
            this.dashArray = new double[strokeArray.length];
            
            // need to apply scaling to dash lengths; use average of x and y scaling sizes...
            AffineTransform transform = graphics.getTransform();
            double scaleFactor = (Math.abs(transform.getScaleX()) + Math.abs(transform.getScaleY()))/2;
            
            for (int i = 0; i < strokeArray.length; i++)
            {
                // scale entries using current transform
                dashArray[i] = scaleFactor*strokeArray[i];
                
                // odd indices are space lengths in Java; these go in as negatives in DXF array
                if (2*(i/2) != i)
                        dashArray[i] = -dashArray[i];
            }
        }
        
    }
    
    
    
    public String getName()
    {
        return name;
    }
    
    
    public void setName(String newName)
    {
        name = newName;
    }
    
    
    /**
     * Equals method for use in determining if a Linetype is already present; overrides Object equals method
     * @param otherLinetype     Another linetype
     * @return                  True if the associated linetype dash arrays are equal
     */
    public boolean equals(Object otherLinetype)
    {
        if ((otherLinetype instanceof DXFLinetype) && (Arrays.equals(this.dashArray, ((DXFLinetype)otherLinetype).dashArray)))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    
    /**
     * Implementation of DXFObject interface method; creates DXF text representing the object.
     */
    public String toDXFString()
    {
        StringBuilder result = new StringBuilder("0\nLTYPE\n");
        
        // print out handle and superclass marker(s)
        result.append(super.toDXFString());
        
        // print out subclass marker
        result.append("100\nAcDbLinetypeTableRecord\n");
        
        result.append("2\n" + name + "\n");
        
        // no flags set
        result.append("70\n0\n");
        
        // alignment code - always 65 (ASCII 'A')
        result.append("72\n65\n");
        
        if (dashArray == null)
        {
            result.append("73\n0\n");
        }
        else
        {
            // number of line type (dash) elements
            result.append("73\n" + dashArray.length + "\n");
            
            // total pattern length
            double patternLength = 0;
            for (int i = 0; i < dashArray.length; i++)
            {
                // sum element lengths; use abs since use negatives for spaces...
                patternLength += Math.abs(dashArray[i]);
            }
            result.append("40\n" + patternLength + "\n");
            
            // dash elements
            for (int i = 0; i < dashArray.length; i++)
            {
                // dash or space length
                result.append("49\n" + dashArray[i] + "\n");
                
                // simple (non-complex) mark
                result.append("74\n0\n");
            }
        }
        
        // descriptive text - nothing
        result.append("3\n\n");
        
        return result.toString();
    }
    
    
    
    public static String getNextName()
    {
        String name = "Linetype_" + linetypeCount;
        linetypeCount++;
        
        return name;
    }
    
}