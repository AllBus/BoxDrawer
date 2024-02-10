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

import java.awt.Color;

/**
 * Class representing a layer for use in LAYER table.
 * @author jsevy
 *
 */
public class DXFLayer extends DXFTableRecord
{
    private String name;
    private Color color;
    protected double linewidth;
    protected DXFLinetype linetype;
    
    
    /**
     * Create a LAYER table record object with specified name.
     * 
     * @param name  name of table record
     */
    public DXFLayer(String name, DXFGraphics graphics)
    {
        this.name = name;
        this.color = graphics.getColor();
        this.linewidth = graphics.getLineWidth();
        this.linetype = graphics.addLinetype();
    }
    
    
    /**
     * Get the name of the layer, for use when generating DXF for entities
     * @return  The name of the layer
     */
    public String getName()
    {
        return name;
    }
    
    
    /**
     * Equals method for use in determining if a Layer is already present; overrides Object equals method
     * @param otherLayer     Another layer
     * @return               True if the associated layer names are equal
     */
    public boolean equals(Object otherLayer)
    {
        if ((otherLayer instanceof DXFLayer) && (this.name.equals(((DXFLayer)otherLayer).name)))
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
        StringBuilder result = new StringBuilder("0\nLAYER\n");
        
        // print out handle and superclass marker(s)
        result.append(super.toDXFString());
        
        // print out subclass marker
        result.append("100\nAcDbLayerTableRecord\n");
        
        result.append("2\n" + name + "\n");
        
        // add linetype
        result.append("6\n" + linetype.getName() + "\n");

        // add thickness; specified in Java in pixels at 72 pixels/inch; needs to be in 1/100 of mm for DXF, and restricted range of values
        result.append("370\n" + getDXFLineWeight(linewidth) + "\n");
       
        // add color number
        result.append("62\n" + DXFColor.getClosestDXFColor(color.getRGB()) + "\n");
        
        // no flags set
        result.append("70\n0\n");
        
        // hard-pointer handle to PlotStyleName object; mandatory, and AutoCAD 2022 got picky about the value - must be 0
        result.append("390\n0\n");
        
        return result.toString();
    }
    
}