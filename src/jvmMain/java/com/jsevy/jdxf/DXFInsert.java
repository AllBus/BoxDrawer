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

import java.awt.geom.AffineTransform;




/**
 * Class representing a line segment.
 * @author jsevy
 *
 */
public class DXFInsert extends DXFGraphicalEntity
{
    private DXFBlock block;
    private RealPoint location;
    private double scaleX;
    private double scaleY;
    private double scaleZ = 1;
    private double rotationAngle;
    
    
    /**
     * Create a DXFInsert entity to insert the supplied DXFBlock into the Entities section of the
     * DXF output. The supplied AffineTransform is used to position/rotate the block.
     * 
     * @param block                 DXFBlock to be inserted into the DXF entities output
     * @param graphicsTransform     Transform to position/rotate the block
     * @param graphics              Associated DXFGraphics object
     */
    public DXFInsert(DXFBlock block, AffineTransform graphicsTransform, DXFGraphics graphics)
    {
        // assign layer and color/line properties
        super(graphics);
        
        this.block = block;
        
        setTransformationParameters(graphicsTransform);
        
    }
    
    
    private void setTransformationParameters(AffineTransform graphicsTransform)
    {
        // get transform parameters
        double m00 = graphicsTransform.getScaleX();
        double m11 = graphicsTransform.getScaleY();
        double m01 = graphicsTransform.getShearX();
        double m10 = graphicsTransform.getShearY();
        double m02 = graphicsTransform.getTranslateX();
        double m12 = graphicsTransform.getTranslateY();
        
        
        // get the location; equals the translation in the graphics transform
        this.location = new RealPoint(m02, -m12, 0);
        
        // get scaling
        scaleX = Math.signum(m00) * Math.sqrt(m00*m00 + m10*m10);
        scaleY = Math.signum(m11) * Math.sqrt(m11*m11 + m01*m01);
        
        // get rotation angle
        if (m11 == 0)
        {
            if (m01 < 0)
                rotationAngle = -Math.PI/2;
            else
                rotationAngle = Math.PI/2;
        }
        else
        {
            rotationAngle = Math.atan(m01/m11);
        }
        
    }
    
    
    
    /**
     * Implementation of DXFObject interface method; creates DXF text representing the line segment.
     */
    public String toDXFString()
    {
        StringBuilder result = new StringBuilder("0\nINSERT\n");
        
        // print out handle and superclass marker(s)
        result.append(super.toDXFString());
        
        // print out subclass marker
        result.append("100\nAcDbBlockReference\n");
        
        result.append("2\n" + block.getName() + "\n");
        
        result.append("10\n" + setPrecision(location.x) + "\n");
        result.append("20\n" + setPrecision(location.y) + "\n");
        result.append("30\n" + setPrecision(location.z) + "\n");
       
        result.append("41\n" + setPrecision(scaleX) + "\n");
        result.append("42\n" + setPrecision(scaleY) + "\n");
        result.append("43\n" + setPrecision(scaleZ) + "\n");
        
        result.append("50\n" + setPrecision(rotationAngle*180/Math.PI) + "\n");
               
        return result.toString();
    }
    
    
    public String getDXFHatchInfo()
    {
        // not sure can use an insert as a hatch boundary definition...
        return "";
    }
}