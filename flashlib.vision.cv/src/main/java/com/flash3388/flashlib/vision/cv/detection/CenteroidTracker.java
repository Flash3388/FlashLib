package com.flash3388.flashlib.vision.cv.detection;

import com.flash3388.flashlib.vision.detection.ObjectTracker;
import com.flash3388.flashlib.vision.detection.ScorableTarget;
import com.jmath.vectors.Vector2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CenteroidTracker implements ObjectTracker<ScorableTarget> {

    private final Map<Integer, ScorableTarget> mTrackedObjects;
    private final Map<Integer, ScorableTarget> mOldObjects;

    private int mNextId;

    public CenteroidTracker() {
        mTrackedObjects = new HashMap<>();
        mOldObjects = new HashMap<>();
        mNextId = 1;
    }

    @Override
    public Map<Integer, ? extends ScorableTarget> updateTracked(Collection<? extends ScorableTarget> objects) {
        List<ScorableTarget> targetsList = new LinkedList<>(objects);
        Map<Integer, ScorableTarget> updates = new HashMap<>();
        Map<Integer, ScorableTarget> knownIds = new HashMap<>(mTrackedObjects);

        Map<Integer, ScorableTarget> tracks = new HashMap<>(mOldObjects);
        tracks.putAll(mTrackedObjects);
        for (Map.Entry<Integer, ScorableTarget> entry : tracks.entrySet()) {
            List<TrackPair> trackPairs = new ArrayList<>();
            for (int i = 0; i < targetsList.size(); i++) {
                trackPairs.add(new TrackPair(entry.getValue(), i, targetsList.get(i)));
            }

            if (trackPairs.isEmpty()) {
                continue;
            }

            // we want the smallest distance match, which will be the first after sorting
            trackPairs.sort(Comparator.comparingDouble(TrackPair::distance));
            TrackPair pair = trackPairs.get(0);
            updates.put(entry.getKey(), pair.mUpdated);
            // we found the target so let's remove it from the list, so we don't use it anymore
            targetsList.remove(pair.mIndex);
            // mark the id as matched
            knownIds.remove(entry.getKey());
        }

        // update changes in tracked objects
        mTrackedObjects.putAll(updates);

        // handle lost objects
        for (Map.Entry<Integer, ScorableTarget> entry : knownIds.entrySet()) {
            mTrackedObjects.remove(entry.getKey());
            mOldObjects.put(entry.getKey(), entry.getValue());
        }

        // add new objects
        for (ScorableTarget target : targetsList) {
            int id = mNextId++;
            mTrackedObjects.put(id, target);
        }

        return Collections.unmodifiableMap(mTrackedObjects);
    }

    private static class TrackPair {

        final ScorableTarget mOriginal;
        final int mIndex;
        final ScorableTarget mUpdated;

        private TrackPair(ScorableTarget original, int index, ScorableTarget updated) {
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
