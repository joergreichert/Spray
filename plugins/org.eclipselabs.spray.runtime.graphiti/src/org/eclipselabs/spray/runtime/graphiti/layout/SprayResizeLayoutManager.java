package org.eclipselabs.spray.runtime.graphiti.layout;

import org.eclipse.graphiti.datatypes.IDimension;
import org.eclipse.graphiti.features.context.ILayoutContext;
import org.eclipse.graphiti.mm.algorithms.GraphicsAlgorithm;
import org.eclipse.graphiti.mm.algorithms.Polygon;
import org.eclipse.graphiti.mm.algorithms.Polyline;
import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipselabs.spray.runtime.graphiti.shape.SprayLayoutManager;

public class SprayResizeLayoutManager extends SprayLayoutManager {

    @Override
    public boolean layout(ILayoutContext context) {
        boolean anythingChanged = false;
        Shape shape = (Shape) context.getPictogramElement();
        GraphicsAlgorithm shapeGa = shape.getGraphicsAlgorithm();

        // Check MinHeight
        if (minSizeHeight > 0 && shapeGa.getHeight() < minSizeHeight) {
            shapeGa.setHeight(minSizeHeight);
            anythingChanged = true;
        }
        // Check MinWidth
        if (minSizeWidth > 0 && shapeGa.getWidth() < minSizeWidth) {
            shapeGa.setWidth(minSizeWidth);
            anythingChanged = true;
        }
        // Check MaxHeight
        if (maxSizeHeight > 0 && shapeGa.getHeight() > maxSizeHeight) {
            shapeGa.setHeight(maxSizeHeight);
            anythingChanged = true;
        }
        // Check MaxHeight
        if (maxSizeWidth > 0 && shapeGa.getWidth() > maxSizeWidth) {
            shapeGa.setWidth(maxSizeWidth);
            anythingChanged = true;
        }
        gaService = Graphiti.getGaService();

        // Get old size values from element properties
        float oldMaxWidth = SprayLayoutService.getCurrentWidth(shape);
        float oldMaxHeight = SprayLayoutService.getCurrentHeight(shape);

        // Get new size of container
        float newContainerWidth = shapeGa.getWidth();
        float newContainerHeight = shapeGa.getHeight();

        // Determine size factors for resizing of elements
        float widthFactor = newContainerWidth / oldMaxWidth;
        float heightFactor = newContainerHeight / oldMaxHeight;

        // Check if something has changed
        if ((widthFactor != 1.0) || (heightFactor != 1.0)) {
            // calculate new element sizes
            if (shape instanceof ContainerShape) {
                ContainerShape containerShape = (ContainerShape) shape;
                resizeElements(containerShape, widthFactor, heightFactor);
                // calculate new anchor positions
                recalculateAnchors(containerShape, widthFactor, heightFactor);
            } else {
                IDimension size = gaService.calculateSize(shapeGa);
                if (shapeGa instanceof Polyline) {
                    resizePolyline((Polyline) shapeGa, size, widthFactor, heightFactor);
                } else if (shapeGa instanceof Polygon) {
                    resizePolygon((Polygon) shapeGa, size, widthFactor, heightFactor);
                }
            }
            // set new size to properties of element
            SprayLayoutService.setCurrentHeight(shape, (int) Math.round(newContainerHeight));
            SprayLayoutService.setCurrentWidth(shape, (int) Math.round(newContainerWidth));

            anythingChanged = true;
        }

        return anythingChanged;
    }

    public void resizeElements(ContainerShape shape, double widthFactor, double heightFactor) {
        GraphicsAlgorithm a = shape.getGraphicsAlgorithm();
        // Special layout handling for shapes which only consists of a polyline
        // or polygon
        if (shape.getChildren().size() == 0 && ((a instanceof Polyline) || (a instanceof Polygon))) {
            IDimension size = gaService.calculateSize(a);
            if (a instanceof Polyline) {
                resizePolyline((Polyline) a, size, widthFactor, heightFactor);
            } else if (a instanceof Polygon) {
                resizePolygon((Polygon) a, size, widthFactor, heightFactor);
            }
        } else {
            // calculate new element size recursively
            resizeElementsRecursive(shape, widthFactor, heightFactor);
        }
    }

    @Override
    public void resizeElementsRecursive(ContainerShape shape, double widthFactor, double heightFactor) {
        GraphicsAlgorithm a = shape.getGraphicsAlgorithm();
        if (shape.getChildren().size() == 0) {
            return;
        } else {
            for (Shape child : shape.getChildren()) {
                GraphicsAlgorithm ga = child.getGraphicsAlgorithm();
                IDimension size = gaService.calculateSize(ga);

                if (SprayLayoutService.getLayoutData(child).isHorizontalStretchable()) {
                    if (ga instanceof Polyline) {
                        resizePolyline((Polyline) ga, size, widthFactor, heightFactor);
                    } else if (ga instanceof Polygon) {
                        resizePolygon((Polygon) ga, size, widthFactor, heightFactor);
                    } else {
                        resizeShape(ga, size, widthFactor, heightFactor);
                    }
                }

                if (child instanceof ContainerShape) {
                    if (!SprayLayoutService.getLayoutData(child).isHorizontalStretchable()) {
                        SprayAbstractLayoutManager.debug("Nested compartment NOT STRETCHEABKE, stopping at " + SprayLayoutService.getId(child));
                        //                        resizeElementsRecursive((ContainerShape) child, widthFactor, heightFactor);
                    } else {
                        resizeElementsRecursive((ContainerShape) child, widthFactor, heightFactor);
                    }
                }

                //                }
            }
        }

    }

}
