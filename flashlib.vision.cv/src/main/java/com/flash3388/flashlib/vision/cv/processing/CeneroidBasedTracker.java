package com.flash3388.flashlib.vision.cv.processing;

import com.jmath.vectors.Vector2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CeneroidBasedTracker implements ObjectTracker {

    private final Map<Integer, Scorable> mTrackedObjects;
    private int mNextId;

    public CeneroidBasedTracker() {
        mTrackedObjects = new HashMap<>();
        mNextId = 1;
    }

    @Override
    public Map<Integer, ? extends Scorable> updateTracked(Collection<? extends Scorable> objects) {
        /*
        Using objects previously detected, compare the currently detected objects
        using the center point (distance between center points) to find the new object
        which most closely matches the old one.

        Unfortunately this does not take into account other properties of objects so
        confusion may occur, especially when many objects are involved.

        Cannot keep track of objects which are lost for even a frame. They will be considered as new objects
        when they are detected again.
         */

        List<Scorable> targetsList = new LinkedList<>(objects);
        Map<Integer, Scorable> updates = new HashMap<>();
        Set<Integer> knownIds = new HashSet<>(mTrackedObjects.keySet());

        for (Map.Entry<Integer, Scorable> entry : mTrackedObjects.entrySet()) {
            List<TrackPair> trackPairs = new ArrayList<>();
            for (int i = 0; i < targetsList.size(); i++) {
                trackPairs.add(new TrackPair(entry.getValue(), i, targetsList.get(i)));
            }

            if (trackPairs.isEmpty()) {
                continue;
            }

            // we want the smallest distance match
            TrackPair pair = Collections.min(trackPairs, Comparator.comparingDouble(TrackPair::distance));
            updates.put(entry.getKey(), pair.mUpdated);
            // we found the target so let's remove it from the list so we don't use it anymore
            targetsList.remove(pair.mIndex);
            // mark the id as matched
            knownIds.remove(entry.getKey());
        }

        // update changes in tracked objects
        mTrackedObjects.putAll(updates);

        // handle lost objects
        for (int id : knownIds) {
            mTrackedObjects.remove(id);
        }

        // add new objects
        for (Scorable target : targetsList) {
            int id = mNextId++;
            mTrackedObjects.put(id, target);
        }

        return Collections.unmodifiableMap(mTrackedObjects);
    }

    private static class TrackPair {

        final Scorable mOriginal;
        final int mIndex;
        final Scorable mUpdated;

        private TrackPair(Scorable original, int index, Scorable updated) {
            mOriginal = original;
            mIndex = index;
            mUpdated = updated;
        }

        double distance() {
            Vector2 originalCenter = mOriginal.getCenter();
            Vector2 updatedCenter = mUpdated.getCenter();

            return Math.sqrt(Math.pow(updatedCenter.x() - originalCenter.x(), 2) +
                    Math.pow(updatedCenter.y() - originalCenter.y(), 2));
        }
    }
}
