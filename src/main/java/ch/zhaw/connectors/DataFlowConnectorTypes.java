/*
 * Copyright (C) 2005 - 2014 by TESIS DYNAware GmbH
 */
package ch.zhaw.connectors;

import javafx.geometry.Side;


/**
 * This class defines 4 connector types. The connectors can be:
 *
 * <ol>
 * <li>Either <b>top</b>, <b>right</b>, <b>bottom</b>, or <b>left</b></li>
 * </ol>
 *
 */
public final class DataFlowConnectorTypes
{

    private DataFlowConnectorTypes()
    {
        // Auto-generated constructor stub
    }

    /**
     * Type string for a connector positioned at the top of a node.
     */
    public static final String TOP = "top";

    /**
     * Type string for a connector positioned on the right side of a
     * node.
     */
    public static final String RIGHT = "right";

    /**
     * Type string for a connector positioned at the bottom of a node.
     */
    public static final String BOTTOM = "bottom";

    /**
     * Type string for a connector positioned on the left side of a node.
     */
    public static final String LEFT = "left";

    private static final String LEFT_SIDE = "left";
    private static final String RIGHT_SIDE = "right";
    private static final String TOP_SIDE = "top";
    private static final String BOTTOM_SIDE = "bottom";

    /**
     * Returns true if the type is supported by the default skins.
     *
     * @param type
     *            a connector's type string
     * @return {@code true} if the type is supported by the default skins
     */
    public static boolean isValid(final String type)
    {
        final boolean hasSide = type != null && (isTop(type) || isRight(type) || isBottom(type) || isLeft(type));
        return hasSide;
    }

    /**
     * Gets the side corresponding to the given connector type.
     *
     * @param type
     *            a non-null connector type
     * @return the {@link Side} the connector type is on
     */
    public static Side getSide(final String type)
    {
        if (isTop(type))
        {
            return Side.TOP;
        }
        else if (isRight(type))
        {
            return Side.RIGHT;
        }
        else if (isBottom(type))
        {
            return Side.BOTTOM;
        }
        else if (isLeft(type))
        {
            return Side.LEFT;
        }
        else
        {
            return null;
        }
    }

    /**
     * Returns true if the type corresponds to a connector positioned at the top
     * of a node.
     *
     * @param type
     *            a connector's type string
     * @return {@code true} if the connector will be positioned at the top of a
     *         node
     */
    public static boolean isTop(final String type)
    {
        return type.contains(TOP_SIDE);
    }

    /**
     * Returns true if the type corresponds to a connector positioned on the
     * right side of a node.
     *
     * @param type
     *            a connector's type string
     * @return {@code true} if the connector will be positioned on the right
     *         side of a node
     */
    public static boolean isRight(final String type)
    {
        return type.contains(RIGHT_SIDE);
    }

    /**
     * Returns true if the type corresponds to a connector positioned at the
     * bottom of a node.
     *
     * @param type
     *            a connector's type string
     * @return {@code true} if the connector will be positioned at the bottom of
     *         a node
     */
    public static boolean isBottom(final String type)
    {
        return type.contains(BOTTOM_SIDE);
    }

    /**
     * Returns true if the type corresponds to a connector positioned on the
     * left side of a node.
     *
     * @param type
     *            a connector's type string
     * @return {@code true} if the connector will be positioned on the left side
     *         of a node
     */
    public static boolean isLeft(final String type)
    {
        return type.contains(LEFT_SIDE);
    }

    /**
     * Returns true if the two given types are on the same side of a node.
     *
     * @param firstType
     *            the first connector type
     * @param secondType
     *            the second connector type
     * @return {@code true} if the connectors are on the same side of a node
     */
    public static boolean isSameSide(final String firstType, final String secondType)
    {
        return getSide(firstType) != null && getSide(firstType).equals(getSide(secondType));
    }
}
