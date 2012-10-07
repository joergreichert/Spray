package org.eclipselabs.spray.runtime.graphiti.layout;

import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;

/**
 * This layout manager is specific for the Diagra, it triggers the full a=layout
 * of all root shapes in the diagram
 * 
 * @author jos
 */
public class SprayDiagramLayoutManager extends SprayAbstractLayoutManager {

    private Diagram diagram;

    public SprayDiagramLayoutManager(Diagram shape) {
        super(shape);
        this.diagram = shape;
    }

    @Override
    public void layout() {
        for (Shape shape : diagram.getChildren()) {
            if (shape instanceof ContainerShape) {
                ContainerShape container = (ContainerShape) shape;
                ISprayLayoutManager mgr = SprayLayoutService.getLayoutManager(container);
                if (mgr != null) {
                    mgr.layout();
                    Graphiti.getGaLayoutService().setLocation(container.getGraphicsAlgorithm(), 10, 10);
                    SprayAbstractLayoutManager.fixOffset(container);
                }
            }

        }
    }

    @Override
    public void stretchWidthTo(int newWidth) {
    }

    @Override
    public void stretchHeightTo(int newHeight) {
    }

}