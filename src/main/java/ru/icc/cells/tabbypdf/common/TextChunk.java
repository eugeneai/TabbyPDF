package ru.icc.cells.tabbypdf.common;

import com.itextpdf.text.pdf.DocumentFont;
import com.itextpdf.text.pdf.parser.Vector;

/**
 * Rectangular area containing text and font characteristics
 */
public class TextChunk extends RectangularTextContainer implements Comparable<TextChunk>
{
    /**
     * the text of the chunk
     */
    private       String       text;
    /**
     * the orientation as a scalar for quick sorting
     */
    private final int          orientationMagnitude;
    /**
     * perpendicular distance to the orientation unit vector (i.e. the Y position in an unrotated coordinate system)
     * we round to the nearest integer to handle the fuzziness of comparing floats
     */
    private final int          distPerpendicular;
    /**
     * distance of the start of the chunk parallel to the orientation unit vector (i.e. the X position in an unrotated coordinate system)
     */
    private final float        distParallelStart;
    /**
     * distance of the end of the chunk parallel to the orientation unit vector (i.e. the X position in an unrotated coordinate system)
     */
    private final float        distParallelEnd;
    /**
     * the width of a single space character in the font of the chunk
     */
    private final float        charSpaceWidth;
    /**
     * the font of the chunk text
     */
    private       DocumentFont font;
    /**
     * the size of the font of the chunk text
     */
    private float fontSize;

    public TextChunk(String string, float left, float bottom, float right, float top, float charSpaceWidth)
    {
        super(left, bottom, right, top);
        this.text = string;
        this.charSpaceWidth = charSpaceWidth;

        Vector start   = new Vector(getLeft(), getBottom(), 0);
        Vector end     = new Vector(getRight(), getBottom(), 0);
        Vector oVector = end.subtract(start);
                /*endLocation.subtract(startLocation);*/
        if (oVector.length() == 0)
        {
            oVector = new Vector(1, 0, 0);
        }
        //unit vector in the orientation of the chunk
        Vector orientationVector = oVector.normalize();
        orientationMagnitude =
                (int) (Math.atan2(orientationVector.get(Vector.I2), orientationVector.get(Vector.I1)) * 1000);

        Vector origin = new Vector(0, 0, 1);
        distPerpendicular = (int) (start.subtract(origin)).cross(orientationVector).get(Vector.I3);

        distParallelStart = orientationVector.dot(start);
        distParallelEnd = orientationVector.dot(end);
    }

    public void setChunkFont(DocumentFont font)
    {
        this.font = font;
    }

    public DocumentFont getChunkFont()
    {
        return this.font;
    }

    public float getFontSize()
    {
        return fontSize;
    }

    public void setFontSize(float fontSize)
    {
        this.fontSize = fontSize;
    }

    public void setText(String text)
    {
        this.text = text;
    }

    @Override
    public String getText()
    {
        return text;
    }

    public float getCharSpaceWidth()
    {
        return charSpaceWidth;
    }

    /**
     * Used for debugging
     */
    public void printDiagnostics()
    {
        System.out.println(
                "Text (@" + getLeft() + "," + getBottom() + " -> " + getRight() + "," + getBottom() + "): " + text);
        System.out.println("orientationMagnitude: " + orientationMagnitude);
        System.out.println("distPerpendicular: " + distPerpendicular);
        System.out.println("distParallel: " + distParallelStart);
    }

    /**
     * Checks whether this chunk is at the same horizontal line with other chunk
     * @param as other chunk
     */
    public boolean sameLine(TextChunk as)
    {
        if (orientationMagnitude != as.orientationMagnitude) return false;
        return distPerpendicular == as.distPerpendicular;
    }

    /**
     * Checks whether this chunk is at the same horizontal line with other chunk
     * @param as other chunk
     */
    public boolean sameLine2(TextChunk as)
    {
        return getBottom() == as.getBottom();
    }


    /**
     * @param other other chunk
     * @return distance from end of other chunk
     */
    public float distanceFromEndOf(TextChunk other)
    {
        return distParallelStart - other.distParallelEnd;
    }

    @Override
    public int compareTo(TextChunk rhs)
    {
        if (this == rhs) return 0;

        int rslt;
        rslt = Integer.compare(orientationMagnitude, rhs.orientationMagnitude);
        if (rslt != 0) return rslt;

        rslt = Integer.compare(distPerpendicular, rhs.distPerpendicular);
        if (rslt != 0) return rslt;

        return Float.compare(distParallelStart, rhs.distParallelStart);
    }
}
