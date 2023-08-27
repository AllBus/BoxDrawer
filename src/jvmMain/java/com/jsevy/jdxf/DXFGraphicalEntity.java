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
 * Just a special case of DXFObjects used for graphical entities.
 * 
 * @author jsevy
 *
 */
public class DXFGraphicalEntity extends DXFEntity
{
    // Layer entity belongs to; null if no layer specified
    //private Color color;
    //private double linewidth;
    //private DXFLinetype linetype;
    
    private int dxfColor;
    private int dxfLinewidth;
    private String linetypeName;
    
    
    //public DXFEntity()
    //{
    //    super();
    //}
    
    
    public DXFGraphicalEntity(DXFGraphics graphics)
    {
        super(graphics.getLayer());
        
        
        
        // if these should be ByBlock or ByLayer, switch
        switch (graphics.blockLayerMode)
        {
            case BYBLOCK:
            {
                this.dxfColor = DXFColor.COLOR_BYBLOCK;
                this.dxfLinewidth = DXFDatabaseObject.LINEWEIGHT_BYBLOCK;
                this.linetypeName = DXFLinetype.LINETYPENAME_BYBLOCK;
                break;
            }
            
            case BYLAYER:
            {
                // if ((this.layer != null) && (!this.layer.getName().equals("0")))
                this.dxfColor = DXFColor.COLOR_BYLAYER;
                this.dxfLinewidth = DXFDatabaseObject.LINEWEIGHT_BYLAYER;
                this.linetypeName = DXFLinetype.LINETYPENAME_BYLAYER;
                break;
                
            }
            
            default:
            {
                // set color/line properties based on current graphics
                this.dxfColor = DXFColor.getClosestDXFColor(graphics.getColor().getRGB());
                this.dxfLinewidth = getDXFLineWeight(graphics.getLineWidth());
                DXFLinetype linetype = graphics.addLinetype();
                this.linetypeName = linetype.getName();
            }
        }
    }
    
    
    public String toDXFString()
    {
        // print out handle and superclass marker(s)
        StringBuilder result = new StringBuilder(super.toDXFString());
        
        // add linetype
        result.append("6\n" + linetypeName + "\n");

        // add thickness; specified in Java in pixels at 72 pixels/inch; needs to be in 1/100 of mm for DXF, and restricted range of values
        result.append("370\n" + dxfLinewidth + "\n");
       
        // add color number
        result.append("62\n" + dxfColor + "\n");
        
        return result.toString();
    }
    
    /* Should be overridden by classes that can be used as Hatch boundaries */
    public String getDXFHatchInfo()
    {
        return "";
    }
    
    
    
    
}