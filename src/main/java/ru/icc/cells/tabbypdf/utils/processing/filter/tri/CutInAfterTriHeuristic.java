package ru.icc.cells.tabbypdf.utils.processing.filter.tri;

import ru.icc.cells.tabbypdf.common.Rectangle;
import ru.icc.cells.tabbypdf.utils.processing.filter.bi.HorizontalPositionBiHeuristic;

public class CutInAfterTriHeuristic extends TriHeuristic<Rectangle>
{
    public CutInAfterTriHeuristic()
    {
        super(Orientation.VERTICAL, TriHeuristicType.AFTER);
    }

    @Override
    public boolean test(Rectangle first, Rectangle second, Rectangle third)
    {
        HorizontalPositionBiHeuristic horizontalPositionBiHeuristic = new HorizontalPositionBiHeuristic();
        return !(first.getLeft() <= second.getRight() && first.getRight() >= third.getLeft() &&
        horizontalPositionBiHeuristic.test(second, third));
    }
}
