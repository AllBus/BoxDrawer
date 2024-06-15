//package com.kos.figure.algorithms;
//
//import com.kos.figure.FigureEllipse;
//
//import java.util.List;
//
//import vectors.Matrix;
//import vectors.Vec2;
//
//public class Ellipse {
//
//    // Create points to simulate ellipse using beziers
//    private FigureEllipse createDXFEllipticalArc(double x, double y, double width, double height, double startAngle, double sweepAngle, Matrix graphicsMatrix) {
//
//        // get oval parameters
//        double xCen = x + width / 2;
//        double yCen = y + height / 2;
//
//        // negate angles since y axis inverted going from Java to DXF
//        startAngle = -startAngle;
//        sweepAngle = -sweepAngle;
//
//        double a = width / 2;
//        double b = height / 2;
//
//        double startParameter = (startAngle * (Math.PI / 180));
//        double endParameter = ((startAngle + sweepAngle) * (Math.PI / 180));
//        double sweepAngleRadians = (sweepAngle * (Math.PI / 180));
//
//        Vec2 center = new Vec2(xCen, yCen);
//        Vec2 startPoint = new Vec2(xCen + a * Math.cos(startParameter), yCen + b * Math.sin(startParameter));
//        Vec2 endPoint = new Vec2(xCen + a * Math.cos(endParameter), yCen + b * Math.sin(endParameter));
//        //Vec2 startPointRelative = Vec2.difference(startPoint, center);
//        //Vec2 endPointRelative = Vec2.difference(endPoint, center);
//
//        // The common case where the transform doesn't rotate the axes - not using this since the general case covers everything,
//        // and this doesn't buy us anything in the ultimate DXF output - still get an ellipse
//        /*
//        if (preservesAxisOrientations(javaTransformMatrix))
//        {
//            Vec2 transformedCenter = Vec2.mapPoint(graphicsMatrix, center);
//            Vec2 transformedStartPoint = Vec2.mapPoint(graphicsMatrix, startPoint);
//            Vec2 transformedEndPoint = Vec2.mapPoint(graphicsMatrix, endPoint);
//
//            // map semi axes; since transformation is just a scaling plus translation, gives us new a and b
//            double[] xSemiAxis = {a, 0};
//            double[] ySemiAxis = {0, b};
//            graphicsMatrix.mapVectors(xSemiAxis);
//            graphicsMatrix.mapVectors(ySemiAxis);
//            a = xSemiAxis[0];
//            b = ySemiAxis[1];
//
//            // need to adjust start and end angles based on java-to-dxf axis flip and clockwise vs counterclockwise angles
//            if (sweepAngleRadians < 0)
//            {
//                startAngleRadians = endAngleRadians;
//                sweepAngleRadians = -sweepAngleRadians;
//            }
//
//
//            DXFEllipse ellipse = new DXFEllipse(transformedCenter, a, b, startParameter, endParameter, paint);
//            return ellipse;
//        }
//        */
//
//        // Here goes the complex stuff for the general transformation case involving rotation and skewing of the ellipse
//
//        // Finding the new major axis under a linear transformation is actually a bit tricky: though an
//        // ellipse is mapped to an ellipse under a linear (even affine) transformation, the major axis
//        // of the original ellipse is not mapped to the transformed ellipse's major axis by the transformation.
//        // To find the new major axis, we do the following: represent points on the ellipse parametrically, as
//        //   (x, y) = (a*cos(t), b*sin(t)) (which we'll use as a column vector).
//        // Representing the linear transformation by the matrix
//        //   (c11, c12)
//        //   (c21, c22),
//        // the points of the linearly transformed ellipse become
//        //   (x, y) = (c11*a*cos(t) + c12*b*sin(t), c21*a*cos(t) + c22*b*sin(t)).
//        // Now the points on the major axis are the points farthest from the center (here
//        // at (0, 0) since we're using just the linear part of the affine transfomation). We can
//        // then find these point by solving for t that maximizes the (square of) the distance from the center:
//        //   D  =  x^2 + y^2  =  (c11*a*cos(t) + c12*b*sin(t))^2  +  (c21*a*cos(t) + c22*b*sin(t))^2
//        // i.e., solve for t such that dD/dt = 0.
//        //   dD/dt  =  2 * (c11*a*cos(t) + c12*b*sin(t)) * (-c11*a*sin(t) + c12*b*cos(t))
//        //           + 2 * (c21*a*cos(t) + c22*b*sin(t)) * (-c21*a*sin(t) + c22*b*cos(t))
//        //          =  2 * { [b^2*(c12^2 + c22^2) - a^2*(c11^2 + c21^2)] * sin(t) * cos(t)
//        //                      + a*b*(c11*c12 + c21*c22) * (cos(t)^2 - sin(t)^2) }
//        // Setting this equal to 0, and dividing by -2 and cos(t), gives
//        //   A*tan(t)^2 + B*tan(t) - A  =  0,
//        //      where  A  =  a*b*(c11*c12 + c21*c22)
//        //      and    B  =  a^2*(c11^2 + c21^2) - b^2*(c12^2 + c22^2)
//        // The quadratic formula then gives us our solution:
//        //   tan(t)  =  (-B +/- sqrt(B^2 - 4*A*C))/(2*A)  =  (-B +/- sqrt(B^2 + 4*A^2))/(2*A)
//
//        // get matrix coefficients
//        double[] basisVects = {1, 0, 0, 1};
//        mapVectors(graphicsMatrix, basisVects);
//        double c11 = basisVects[0];
//        double c12 = basisVects[2];
//        double c21 = basisVects[1];
//        double c22 = basisVects[3];
//
//        double A = a * b * (c11 * c12 + c21 * c22);
//        double B = a * a * (c11 * c11 + c21 * c21) - b * b * (c12 * c12 + c22 * c22);
//
//        double tangent1;
//        //double tangent2;
//        if (A != 0) {
//            tangent1 = (-B + Math.sqrt(B * B + 4 * A * A)) / (2 * A);
//            //tangent2 = (-B - Math.sqrt(B*B + 4*A*A))/(2*A);
//        } else {
//            tangent1 = 0;
//            //tangent2 = 0;
//        }
//
//        double t1 = Math.atan(tangent1);
//        double t2 = t1 + Math.PI / 2;
//
//        double majorAxisParameter;
//        double minorAxisParameter;
//        double majorAxisLength;
//        double minorAxisLength;
//
//        // see which is the major axis
//        double D1 = ((c11 * a * Math.cos(t1) + c12 * b * Math.sin(t1)) * (c11 * a * Math.cos(t1) + c12 * b * Math.sin(t1)) + (c21 * a * Math.cos(t1) + c22 * b * Math.sin(t1)) * (c21 * a * Math.cos(t1) + c22 * b * Math.sin(t1)));
//        double D2 = ((c11 * a * Math.cos(t2) + c12 * b * Math.sin(t2)) * (c11 * a * Math.cos(t2) + c12 * b * Math.sin(t2)) + (c21 * a * Math.cos(t2) + c22 * b * Math.sin(t2)) * (c21 * a * Math.cos(t2) + c22 * b * Math.sin(t2)));
//
//        if (D1 >= D2) {
//            majorAxisParameter = t1;
//            minorAxisParameter = t2;
//            majorAxisLength = Math.sqrt(D1);
//            minorAxisLength = Math.sqrt(D2);
//        } else {
//            majorAxisParameter = t2;
//            minorAxisParameter = t1;
//            majorAxisLength = Math.sqrt(D2);
//            minorAxisLength = Math.sqrt(D1);
//        }
//
//        double transformedAxisRatio = minorAxisLength / majorAxisLength;
//
//
//        // get the transformed center, major and (relative) minor aces
//        Vec2 transformedCenter = Vec2.mapPoint(graphicsMatrix, center);
//        Vec2 transformedMajorAxisRelative = new Vec2(c11 * a * Math.cos(majorAxisParameter) + c12 * b * Math.sin(majorAxisParameter), c21 * a * Math.cos(majorAxisParameter) + c22 * b * Math.sin(majorAxisParameter), 0);
//        Vec2 transformedMinorAxisRelative = new Vec2(c11 * a * Math.cos(minorAxisParameter) + c12 * b * Math.sin(minorAxisParameter), c21 * a * Math.cos(minorAxisParameter) + c22 * b * Math.sin(minorAxisParameter), 0);
//
//        // Since angles in dxf always go counterclockwise, we want the minor axis which is counterclockwise from the major axis; check the cross product
//        // to see if we have the right one, and negate if not
//        Vec2 crossProduct = Vec2.crossProduct(transformedMajorAxisRelative, transformedMinorAxisRelative);
//        if (crossProduct.z < 0)
//            transformedMinorAxisRelative = Vec2.scalarProduct(-1, transformedMinorAxisRelative);
//
//        // get transformed start and end points for center lines if needed, as well as to find corresponding ellipse parameters
//        Vec2 transformedStartPoint = Vec2.mapPoint(graphicsMatrix, startPoint);
//        Vec2 transformedEndPoint = Vec2.mapPoint(graphicsMatrix, endPoint);
//        Vec2 transformedStartPointRelative = Vec2.difference(transformedStartPoint, transformedCenter);
//        Vec2 transformedEndPointRelative = Vec2.difference(transformedEndPoint, transformedCenter);
//
//        // want the arc that goes between the start and end points; but need to supply the start and end parametric t values,
//        // and these are relative to the major axis. So we need to find the t values corresponding to the start and end points.
//        // To do this, we decompose relative to the major and minor axes and use the basic ellipse equation to find the corresponding t values.
//        // Let (u, v) represent the coordinates of a point on the ellipse in the major/minor axis coordinate system. Then
//        //    (u, v)  =  (majorAxisLength*cos(t), minorAxisLength*sin(t)),  or
//        //    v/u  =  minorAxisLength/majorAxisLength*tan(t), or   t = atan((v*majorAxisLength)/(u*minorAxisLength)), where
//        //    u  =  startVector dotproduct majorAxisVector/magnitude(majorAxisVector), and v  =  same with minorAxisVector
//
//        double transformedStartParameter;
//        double transformedEndParameter;
//
//        double uStart = Vec2.dotProduct(transformedStartPointRelative, transformedMajorAxisRelative) / Vec2.magnitude(transformedMajorAxisRelative);
//        double vStart = Vec2.dotProduct(transformedStartPointRelative, transformedMinorAxisRelative) / Vec2.magnitude(transformedMinorAxisRelative);
//        if (uStart == 0) {
//            if (vStart > 0)
//                transformedStartParameter = Math.PI / 2;
//            else
//                transformedStartParameter = -Math.PI / 2;
//        } else {
//            transformedStartParameter = Math.atan((vStart * majorAxisLength) / (uStart * minorAxisLength));
//            if (uStart < 0) {
//                transformedStartParameter += Math.PI;
//            }
//        }
//
//
//        double uEnd = Vec2.dotProduct(transformedEndPointRelative, transformedMajorAxisRelative) / Vec2.magnitude(transformedMajorAxisRelative);
//        double vEnd = Vec2.dotProduct(transformedEndPointRelative, transformedMinorAxisRelative) / Vec2.magnitude(transformedMinorAxisRelative);
//        if (uEnd == 0) {
//            if (vEnd > 0)
//                transformedEndParameter = Math.PI / 2;
//            else
//                transformedEndParameter = -Math.PI / 2;
//        } else {
//            transformedEndParameter = Math.atan((vEnd * majorAxisLength) / (uEnd * minorAxisLength));
//            if (uEnd < 0) {
//                transformedEndParameter += Math.PI;
//            }
//        }
//
//        // still need to handle the orientation of the start and end parameters; need to supply them in the right
//        // order so we get the proper arc. Since angles in Java go clockwise, we'll need to flip the start and end parameters
//        // unless the sweep angle's negative or we don't flip the basis vector orientations. We determine the basic vector
//        // orientations using the cross product.
//        double dxfStartParameter = transformedStartParameter;
//        double dxfEndParameter = transformedEndParameter;
//        boolean dxfIsCounterclockwise = true;
//
//        Vec2 basisVectorI = new Vec2(1, 0, 0);
//        Vec2 basisVectorJ = new Vec2(0, 1, 0);
//        Vec2 transformedBasisVectorI = Vec2.mapVector(graphicsMatrix, basisVectorI);
//        Vec2 transformedBasisVectorJ = Vec2.mapVector(graphicsMatrix, basisVectorJ);
//        Vec2 transformedBasisVectorCrossProduct = Vec2.crossProduct(transformedBasisVectorI, transformedBasisVectorJ);
//        double orientationSign = transformedBasisVectorCrossProduct.z;
//
//        if (orientationSign * sweepAngle < 0) {
//            dxfStartParameter = transformedEndParameter;
//            dxfEndParameter = transformedStartParameter;
//            dxfIsCounterclockwise = false;
//        }
//
//        // cover special case where sweep angle >= 2*PI or <= -2*PI
//        if (Math.abs(sweepAngleRadians) >= 2 * Math.PI) {
//            dxfEndParameter = dxfStartParameter + 2 * Math.PI;
//        }
//
//        //System.out.println("Arc:  start point: (" + transformedStartPoint.x + ", " + transformedStartPoint.y + ")");
//        //System.out.println("        end point: (" + transformedEndPoint.x + ", " + transformedEndPoint.y + ")");
//        //System.out.println("      start angle: " + dxfStartParameter);
//        //System.out.println("        end angle: " + dxfEndParameter);
//        //System.out.println(" counterclockwise: " + dxfIsCounterclockwise);
//
//        // finally, create an ellipse or arc or circle with the given parameters
//
//            return new FigureEllipse(
//                    transformedCenter,
//                    transformedMajorAxisRelative,
//                    transformedAxisRatio,
//                    0.0,
//                    dxfStartParameter,
//                    dxfEndParameter);
//        }
//    }
//}
