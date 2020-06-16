package com.larscheng.www.service;

import ch.hsr.geohash.BoundingBox;
import ch.hsr.geohash.GeoHash;
import com.spatial4j.core.context.jts.JtsSpatialContext;
import com.spatial4j.core.io.ShapeReader;
import com.spatial4j.core.io.WKTReader;
import com.spatial4j.core.shape.Rectangle;
import com.spatial4j.core.shape.Shape;
import com.spatial4j.core.shape.SpatialRelation;
import com.spatial4j.core.shape.impl.RectangleImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ch.hsr.geohash.GeoHash.FIRST_BIT_FLAGGED;

@Component
@Slf4j
public class GeoHashQuerySevice {

    @Value("${account.enableCache}")
    private boolean enableC;

    public String getAllGeoHash(String polygon, int len) {
        // get bbox to filter geohashes
        // filter include and interact geohashes
        List<GeoHash> geoHashesInBox = filterGeoHashByBBox(polygon, len);

        List<GeoHash> include = new ArrayList<GeoHash>();
        List<GeoHash> interact = new ArrayList<GeoHash>();
        exactFilter(include, interact, polygon, geoHashesInBox);
        String includes = include.stream().map(it -> it.toBase32()).collect(Collectors.joining(","));
        String interacts = interact.stream().map(it -> it.toBase32()).collect(Collectors.joining(","));
        return String.format("%s;%s", includes, interacts);
    }

    public boolean getConfig() {

        return enableC;
    }

    private void exactFilter(List<GeoHash> include, List<GeoHash> interact, String polygon, List<GeoHash> source) {
        Shape shape = getShape(polygon);

        source.stream().forEach(it -> {
            Shape geoHashShape = geoHash2Shape(it);
            SpatialRelation relate = shape.relate(geoHashShape);
            if (relate == SpatialRelation.INTERSECTS) {
                interact.add(it);
            } else if (relate == SpatialRelation.CONTAINS) {
                include.add(it);
            }
        });
    }

    private Shape geoHash2Shape(GeoHash geoHash) {
        BoundingBox boundingBox = geoHash.getBoundingBox();
        return new RectangleImpl(boundingBox.getMinLat(), boundingBox.getMaxLat(), boundingBox.getMinLon(), boundingBox.getMaxLon(), JtsSpatialContext.GEO);

//        String wktStr = String.format("POLYGON((%f %f, %f %f,%f %f,%f %f,%f %f))", boundingBox.getMinLon(), boundingBox.getMaxLat(), boundingBox.getMaxLon(), boundingBox.getMaxLat(), boundingBox.getMaxLon(), boundingBox.getMinLat(), boundingBox.getMinLon(), boundingBox.getMinLat(), boundingBox.getMinLon(), boundingBox.getMaxLat());
//        WKTReader wktReader = (WKTReader) JtsSpatialContext.GEO.getFormats().getWktReader();
//        try {
//            return wktReader.parse(wktStr);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        return null;
    }

    private List<GeoHash> filterGeoHashByBBox(String polygon, int len) {
        List<GeoHash> results = new ArrayList<GeoHash>();
        try {
            Shape read = getShape(polygon);
            Rectangle boundingBox = read.getBoundingBox();
            GeoHash leftBottom = GeoHash.withCharacterPrecision(boundingBox.getMinY(), boundingBox.getMinX(), len);
            GeoHash rightTop = GeoHash.withCharacterPrecision(boundingBox.getMaxY(), boundingBox.getMaxX(), len);
            int width = getWidth(leftBottom, rightTop, len);
            int height = getHeight(leftBottom, rightTop, len);

            for (int i = 0; i < height; i++) {
                GeoHash rowStart = getRowStart(leftBottom, i);
                GeoHash cur = rowStart;
                for (int j = 0; j < width; j++) {
                    results.add(cur);
                    cur = cur.getEasternNeighbour();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
    }

    private Shape getShape(String polygon) {
        try {
            return JtsSpatialContext.GEO.getFormats().getWktReader().read(polygon);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    private GeoHash getRowStart(GeoHash leftBottom, int i) {
        GeoHash cur = leftBottom;
        for (int j = 0; j < i; j++) {
            cur = cur.getNorthernNeighbour();
        }
        return cur;
    }

    private int getHeight(GeoHash leftBottom, GeoHash rightTop, int len) {
        long l1 = extractEverySecondBit(rightTop.longValue() << 1, len % 2 == 0 ? len / 2 * 5 : (len / 2 * 5 + 2));
        long l2 = extractEverySecondBit(leftBottom.longValue() << 1, len % 2 == 0 ? len / 2 * 5 : (len / 2 * 5 + 2));
        return (int) (l1 - l2 + 1);
    }

    private int getWidth(GeoHash leftBottom, GeoHash rightTop, int len) {
        long l1 = extractEverySecondBit(rightTop.longValue(), len % 2 == 0 ? len / 2 * 5 : (len / 2 * 5 + 3));
        long l2 = extractEverySecondBit(leftBottom.longValue(), len % 2 == 0 ? len / 2 * 5 : (len / 2 * 5 + 3));
        return (int) (l1 - l2 + 1);
    }

    private long extractEverySecondBit(long copyOfBits, int numberOfBits) {
        long value = 0;
        for (int i = 0; i < numberOfBits; i++) {
            if ((copyOfBits & FIRST_BIT_FLAGGED) == FIRST_BIT_FLAGGED) {
                value |= 0x1;
            }
            value <<= 1;
            copyOfBits <<= 2;
        }
        value >>>= 1;
        return value;
    }

}
