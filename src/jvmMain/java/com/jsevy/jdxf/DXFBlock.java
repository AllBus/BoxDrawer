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

import java.awt.Shape;
import java.util.Vector;

/**
 * Class representing a block for use in BLOCKs table.
 * @author jsevy
 *
 */
public class DXFBlock extends DXFEntity
{
    // keep track of blocks created from Shapes so we can look them up
    private static int shapeCount = 1;
    
    
    private String name;
    Shape shape = null;
    private Vector<Vector<DXFGraphicalEntity>> boundaries = new Vector<Vector<DXFGraphicalEntity>>();
    
    
    
    /**
     * Create a BLOCK table record object with specified name.
     * 
     * @param name	name of table record
     */
    public DXFBlock(String name)
    {
        // just assign block end to default layer
        super(null);
        
        this.name = name;
    }
    
    
    /**
     * Create a BLOCK table record object from the given Java Shape object using the
     * supplied boundaries as the block's entities. The Shape is used to compare Blocks
     * to see if they're equivalent.
     * 
     * @param shape         Java Shape from which the block is created
     * @param boundaries    DXF entities representing the block; derived from the Shape
     */
    public DXFBlock(Shape shape, Vector<Vector<DXFGraphicalEntity>> boundaries)
    {
        // just assign block end to default layer
        super(null);
        
        // blocks defined by shapes are named sequentially "SHAPE0", "SHAPE1", etc.
        this.name = "SHAPE" + shapeCount;
        shapeCount++;
        
        // keep a copy of the shape to test if already have an equivalent block
        this.shape = DXFGraphics.cloneShape(shape);
        
        this.boundaries = boundaries;
    }
    
    
    protected String getName()
    {
        return name;
    }
    
    
    /**
     * Set current shape count; used only during initialization in DXFDocument
     * 
     * @param count  Shape count
     */
    protected static void setShapeCount(int count)
    {
        shapeCount = count;
    }
    
    
    /**
     * Implementation of DXFObject interface method; creates DXF text representing the object.
     * Note that this must be followed by a DXFBlockEnd object
     */
    public String toDXFString()
    {
        StringBuilder result = new StringBuilder("0\nBLOCK\n");
        
        // print out handle and superclass marker(s)
        result.append(super.toDXFString());
        
        // print out subclass marker
        result.append("100\nAcDbBlockBegin\n");
        
        // print out name
        result.append("2\n" + name + "\n");
        
        // no flags set
        result.append("70\n0\n");
        
        // block left corner
        result.append("10\n0\n");
        result.append("20\n0\n");
        result.append("30\n0\n");
        
        // print out name again?
        result.append("3\n" + name + "\n");
        
        // xref path name - nothing
        result.append("1\n\n");
        
        // now print out entities for this block if any
        for (int i = 0; i < boundaries.size(); i++)
        {
            Vector<DXFGraphicalEntity> entities = boundaries.elementAt(i);
            
            for (int j = 0; j < entities.size(); j++)
            {
                result.append(entities.elementAt(j).toDXFString());
            }
        }
        
        return result.toString();
    }
    
    
    
    
}