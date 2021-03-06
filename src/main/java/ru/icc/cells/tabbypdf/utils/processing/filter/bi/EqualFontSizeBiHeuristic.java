package ru.icc.cells.tabbypdf.utils.processing.filter.bi;

import ru.icc.cells.tabbypdf.common.TextBlock;
import ru.icc.cells.tabbypdf.common.TextChunk;

public class EqualFontSizeBiHeuristic extends BiHeuristic<TextBlock>
{
    public EqualFontSizeBiHeuristic()
    {
        super(Orientation.BOTH);
    }

    public EqualFontSizeBiHeuristic(Orientation orientation)
    {
        super(orientation);
    }

    @Override
    public boolean test(TextBlock first, TextBlock second)
    {
        for (TextChunk chunk : first.getChunks())
        {
            for (TextChunk textChunk : second.getChunks())
            {
                if (chunk.getFontSize() == textChunk.getFontSize()) return true;
            }
        }
        return false;
    }
}
