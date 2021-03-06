/** ****************************************************************************
 * Copyright (c)  The Spray Project.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Spray Dev Team - initial API and implementation
 **************************************************************************** */
package org.eclipselabs.spray.runtime.graphiti.layout;

import org.eclipse.graphiti.mm.pictograms.ContainerShape;
import org.eclipse.graphiti.mm.pictograms.Diagram;
import org.eclipse.graphiti.mm.pictograms.PictogramElement;
import org.eclipse.graphiti.mm.pictograms.Shape;
import org.eclipse.graphiti.services.Graphiti;
import org.eclipse.graphiti.services.IPeService;
import org.eclipselabs.spray.runtime.graphiti.GraphitiProperties;
import org.eclipselabs.spray.runtime.graphiti.ISprayConstants;
import org.eclipselabs.spray.runtime.graphiti.layout.SprayLayoutData.Alignment;

/**
 * A collection of methods to get layout information for shapes that cannot be stored inside the Graphiti shapes themselves.
 * 
 * @author Jos Warmer (jos.warmer@openmodeling.nl)
 */
public class SprayLayoutService {
    public enum SprayAlignment {
        NONE,
        START,
        MIDDLE,
        END,
        FILL;
    };

    private SprayLayoutService() {
    }

    static final protected String LAYOUT_FLEXIBLE                   = "L_FLEX";
    static final protected String LAYOUT_TYPE                       = "L_TYPE";
    static final protected String LAYOUT_SPACING                    = "L_SPACING";
    static final protected String LAYOUT_MARGIN                     = "L_MARGIN";
    static final protected String LAYOUT_MIN_WIDTH                  = "L_MINW";
    static final protected String LAYOUT_MIN_HEIGHT                 = "L_MINH";
    static final protected String LAYOUT_VISIBLE                    = "L_VIS";
    static final protected String ID                                = "ID";
    static final protected String LAYOUT_NOT_HORIZONTAL_STRETCHABLE = "L_H_STR";
    static final protected String LAYOUT_NOT_VERTICAL_STRETCHABLE   = "L_V_STR";
    static final protected String LAYOUT_ALIGNMENT                  = "L_ALIGN";

    static private IPeService     peService                         = Graphiti.getPeService();

    static public String getId(PictogramElement shape) {
        return (shape.isActive() ? "[A]" : "") + peService.getPropertyValue(shape, ID);
    }

    static public void setId(PictogramElement shape, String id) {
        peService.setPropertyValue(shape, ID, id);
    }

    static public void setLayoutManager(Shape shape, SprayLayoutType type, int margin, int spacing, SprayAlignment alignment) {
        GraphitiProperties.set(shape, LAYOUT_TYPE, type.name());
        GraphitiProperties.set(shape, LAYOUT_SPACING, spacing);
        GraphitiProperties.set(shape, LAYOUT_MARGIN, margin);
        GraphitiProperties.set(shape, LAYOUT_ALIGNMENT, alignment);
    }

    static public void setLayoutManager(Shape shape, SprayLayoutType type, int margin, int spacing) {
        GraphitiProperties.set(shape, LAYOUT_TYPE, type.name());
        GraphitiProperties.set(shape, LAYOUT_SPACING, spacing);
        GraphitiProperties.set(shape, LAYOUT_MARGIN, margin);
    }

    static public void setLayoutManager(Shape shape, SprayLayoutType type, int margin, int spacing, boolean flexible) {
        GraphitiProperties.set(shape, LAYOUT_TYPE, type.name());
        GraphitiProperties.set(shape, LAYOUT_SPACING, spacing);
        GraphitiProperties.set(shape, LAYOUT_MARGIN, margin);
        GraphitiProperties.set(shape, LAYOUT_FLEXIBLE, flexible);
    }

    static public void setLayoutManager(Shape shape, SprayLayoutType type) {
        GraphitiProperties.set(shape, LAYOUT_TYPE, type.name());
    }

    static public void setLayoutData(Shape shape, int minWidth, int minHeight, Alignment horizontalAlignment, Alignment verticalAlignment, boolean strechable) {
        GraphitiProperties.set(shape, LAYOUT_MIN_WIDTH, minWidth);
        GraphitiProperties.set(shape, LAYOUT_MIN_HEIGHT, minHeight);
        GraphitiProperties.set(shape, LAYOUT_NOT_HORIZONTAL_STRETCHABLE, !strechable);
        GraphitiProperties.set(shape, LAYOUT_VISIBLE, true);
    }

    // static public void setLayoutData(Shape shape, int minWidth, int
    // minHeight,
    // Alignment horizontalAlignment, Alignment verticalAlignment) {
    // GraphitiProperties.set(shape, LAYOUT_MIN_WIDTH, minWidth);
    // GraphitiProperties.set(shape, LAYOUT_MIN_HEIGHT, minHeight);
    // GraphitiProperties.set(shape, LAYOUT_VISIBLE, true);
    // }

    static public void setLayoutData(Shape shape, int minWidth, int minHeight) {
        GraphitiProperties.set(shape, LAYOUT_MIN_WIDTH, minWidth);
        GraphitiProperties.set(shape, LAYOUT_MIN_HEIGHT, minHeight);
        GraphitiProperties.set(shape, LAYOUT_VISIBLE, true);
    }

    static public void setLayoutData(Shape shape, int minWidth, int minHeight, boolean strechable) {
        GraphitiProperties.set(shape, LAYOUT_MIN_WIDTH, minWidth);
        GraphitiProperties.set(shape, LAYOUT_MIN_HEIGHT, minHeight);
        GraphitiProperties.set(shape, LAYOUT_NOT_HORIZONTAL_STRETCHABLE, !strechable);
        GraphitiProperties.set(shape, LAYOUT_VISIBLE, true);
    }

    static public SprayLayoutData getLayoutData(Shape shape) {
        return new SprayLayoutData(shape);
    }

    static private SprayLayoutType getLayoutType(Shape shape) {
        String type = peService.getPropertyValue(shape, LAYOUT_TYPE);
        if (type == null) {
            return SprayLayoutType.NONE;
        } else {
            try {
                return SprayLayoutType.valueOf(SprayLayoutType.class, type);
            } catch (IllegalArgumentException exception) {
                return SprayLayoutType.NONE;
            }
        }
    }

    /**
     * returns the layout manager for 'shape'
     * 
     * @param shape
     *            The shape for which the layout manager needs to be found, may
     *            not be null
     * @return
     */
    static public ISprayLayoutManager getLayoutManager(Shape shape) {
        if (shape instanceof Diagram) {
            return new SprayDiagramLayoutManager((Diagram) shape);
        } else if (shape instanceof ContainerShape) {
            return getContainerLayoutManager((ContainerShape) shape);
        } else {
            return new SprayShapeLayoutManager(shape);
        }
    }

    /**
     * returns the layout manager for 'shape'
     * 
     * @param shape
     *            The shape for which the layout manager needs to be found, may
     *            not be null
     * @return
     */
    static private SprayAbstractLayoutManager getContainerLayoutManager(ContainerShape shape) {
        SprayLayoutType type = getLayoutType(shape);
        switch (type) {
        case NONE:
            return new SprayFixedLayoutManager(shape);
        case HORIZONTAL:
            return new SprayHorizontalLayoutManager(shape);
        case VERTICAL:
            return new SprayVerticalLayoutManager(shape);
        case FIXED:
            return new SprayFixedLayoutManager(shape);
        case FIT:
            return new SprayFitLayoutManager(shape);
        case TOP:
            return new SprayTopLayoutManager(shape);
        default:
            return new SprayFixedLayoutManager(shape);
        }
    }

    static public boolean isShapeFromDsl(PictogramElement shape) {
        return GraphitiProperties.getBooleanValue(shape, ISprayConstants.IS_SHAPE_FROM_DSL);
    }

    static public void setShapeFromDsl(PictogramElement shape, boolean value) {
        GraphitiProperties.set(shape, ISprayConstants.IS_SHAPE_FROM_DSL, value);
    }

    static public boolean isCompartment(PictogramElement shape) {
        return GraphitiProperties.getBooleanValue(shape, ISprayConstants.IS_COMPARTMENT);
    }

    static public void setCompartment(PictogramElement shape, boolean value) {
        GraphitiProperties.set(shape, ISprayConstants.IS_COMPARTMENT, value);
        GraphitiProperties.set(shape, ISprayConstants.PROPERTY_CAN_MOVE, false);
    }

    static public int getCurrentWidth(PictogramElement pe) {
        return GraphitiProperties.getIntValue(pe, ISprayConstants.WIDTH_KEY);
    }

    static public void setCurrentWidth(PictogramElement pe, int currentWidth) {
        GraphitiProperties.set(pe, ISprayConstants.WIDTH_KEY, currentWidth);
    }

    static public int getCurrentHeight(PictogramElement pe) {
        return GraphitiProperties.getIntValue(pe, ISprayConstants.HEIGHT_KEY);
    }

    static public void setCurrentHeight(PictogramElement pe, int currentHeight) {
        GraphitiProperties.set(pe, ISprayConstants.HEIGHT_KEY, currentHeight);
    }

    static public boolean isFlexible(PictogramElement pe) {
        return GraphitiProperties.getBooleanValue(pe, LAYOUT_FLEXIBLE);
    }

    static public Diagram findDiagram(Shape shape) {
        if (shape instanceof Diagram) {
            return (Diagram) shape;
        }
        ContainerShape result = shape.getContainer();
        while ((result != null) && !(result instanceof Diagram)) {
            result = result.getContainer();
        }
        return (Diagram) result;
    }

    static public ContainerShape findDslShape(PictogramElement shape) {
        if (SprayLayoutService.isShapeFromDsl(shape)) {
            return (ContainerShape) shape;
        }
        ContainerShape result = null;
        if (shape instanceof Shape) {
            result = ((Shape) shape).getContainer();
            while ((result != null) && (!SprayLayoutService.isShapeFromDsl(result))) {
                result = result.getContainer();
            }
        }
        return result;
    }

    static public ContainerShape findTopDslShape(PictogramElement shape) {
        if (SprayLayoutService.isShapeFromDsl(shape) && ((Shape) shape) instanceof Diagram) {
            return (ContainerShape) shape;
        }
        ContainerShape result = null;
        if (shape instanceof Shape) {
            result = ((Shape) shape).getContainer();
            while ((result != null) && (!SprayLayoutService.isShapeFromDsl(result)) && (!(result.getContainer() instanceof Diagram))) {
                result = result.getContainer();
            }
        }
        return result;
    }
}
