package com.flash3388.flashlib.vision.cv.detection;

import com.flash3388.flashlib.vision.detection.ObjectTracker;
import com.flash3388.flashlib.vision.detection.ScorableTarget;
import com.flash3388.flashlib.vision.detection.Target;
import com.jmath.vectors.Vector2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CenteroidTracker implements ObjectTracker<Target> {

    private final Map<Integer, Target> mTrackedObjects;
    private final Map<Integer, Target> mOldObjects;

    private int mNextId;

    public CenteroidTracker() {
        mTrackedObjects = new HashMap<>();
        mOldObjects = new HashMap<>();
        mNextId = 1;
    }

    @Override
    public Map<Integer, ? extends Target> updateTracked(Collection<? extends Target> objects) {
        List<Target> targetsList = new LinkedList<>(objects);
        Map<Integer, Target> updates = new HashMap<>();
        Map<Integer, Target> knownIds = new HashMap<>(mTrackedObjects);

        Map<Integer, Target> tracks = new HashMap<>(mOldObjects);
        tracks.putAll(mTrackedObjects);
        for (Map.Entry<Integer, Target> entry : tracks.entrySet()) {
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
            // we found the target so let's remove it from the list so we don't use it anymore
            targetsList.remove(pair.mIndex);
            // mark the id as matched
            knownIds.remove(entry.getKey());
        }

        // update changes in tracked objects
        mTrackedObjects.putAll(updates);

        // handle lost objects
        for (Map.Entry<Integer,Target> entry : knownIds.entrySet()) {
            mTrackedObjects.remove(entry.getKey());
            mOldObjects.put(entry.getKey(), entry.getValue());
        }

        // add new objects
        for (Target target : targetsList) {
            int id = mNextId++;
            mTrackedObjects.put(id, target);
        }

        return Collections.unmodifiableMap(mTrackedObjects);
    }

    private static class TrackPair {

        final Target mOriginal;
        final int mIndex;
        final Target mUpdated;

        private TrackPair(Target original, int index, Target updated) {
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
