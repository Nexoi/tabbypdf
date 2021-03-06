package ru.icc.cells.tabbypdf.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Page object contains raw data from PDF page.
 */
public class Page extends Rectangle
{
    private final List<TextChunk>                 originChunks;
    private final List<TextChunk>                 characterChunks;
    private final List<TextChunk>                 wordChunks;
    private final List<Ruling>                    rulings;
    private final List<Rectangle>                 imageRegions;
    private final Map<TextChunk, List<TextChunk>> mapping;

    public Page(float left, float bottom, float right, float top,
                List<TextChunk> originChunks, List<TextChunk> characterChunks, List<TextChunk> wordChunks,
                List<Ruling> rulings, List<Rectangle> imageRegions, Map<TextChunk, List<TextChunk>> mapping)
    {
        super(left, bottom, right, top);
        this.originChunks = originChunks;
        this.characterChunks = characterChunks;
        this.wordChunks = wordChunks;
        this.rulings = rulings;
        this.imageRegions = imageRegions;
        this.mapping = mapping;
    }

    /**
     * Chunks represented by PDF content stream instructions like 'TJ' etc...
     */
    public List<TextChunk> getOriginChunks()
    {
        return originChunks;
    }

    /**
     * Rulings represented by PDF content stream instructions like 'LINETO' etc...
     */
    public List<Ruling> getRulings()
    {
        return rulings;
    }

    /**
     * Chunks compiled from each character render info
     */
    public List<TextChunk> getCharacterChunks()
    {
        return characterChunks;
    }

    /**
     * Chunks combined into words from character chunks
     */
    public List<TextChunk> getWordChunks()
    {
        return wordChunks;
    }

    public List<Rectangle> getImageRegions()
    {
        return imageRegions;
    }

    public Page getRegion(float left, float bottom, float right, float top)
    {
        return getRegion(new Rectangle(left, bottom, right, top));
    }

    public Map<TextChunk, List<TextChunk>> getMapping()
    {
        return mapping;
    }

    public Page getRegion(Rectangle bound)
    {
        List<TextChunk> originChunks =
                this.originChunks
                        .stream()
                        .filter(bound::intersects)
                        .collect(Collectors.toList());
        List<TextChunk> characterChunks =
                this.characterChunks
                        .stream()
                        .filter(bound::intersects)
                        .collect(Collectors.toList());
        List<TextChunk> wordChunks =
                this.wordChunks
                        .stream()
                        .filter(bound::intersects)
                        .collect(Collectors.toList());
        List<Ruling> rulings =
                this.rulings
                        .stream()
                        .filter(ruling -> bound.intersects(
                                new Rectangle((float) ruling.getStartLocation().getX(),
                                              (float) ruling.getStartLocation().getY(),
                                              (float) ruling.getEndLocation().getX(),
                                              (float) ruling.getEndLocation().getY())))
                        .collect(Collectors.toList());
        Map<TextChunk, List<TextChunk>> newMapping = new HashMap<>(mapping);
/*        for (TextChunk chunk : mapping.keySet()) {
            if (!bound.intersects(chunk)){
                newMapping.remove(chunk);
            }
        }*/
        mapping.keySet()
               .stream()
               .filter(chunk -> !bound.intersects(chunk))
               .forEach(newMapping::remove);


        List<Rectangle> imageRegions =
                this.imageRegions
                        .stream()
                        .filter(bound::intersects)
                        .collect(Collectors.toList());
        return new Page(bound.getLeft(), bound.getBottom(), bound.getRight(), bound.getTop(),
                        originChunks, characterChunks, wordChunks,
                        rulings, imageRegions, newMapping);
    }
}
