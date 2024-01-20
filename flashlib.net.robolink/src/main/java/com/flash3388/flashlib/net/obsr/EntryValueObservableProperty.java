package com.flash3388.flashlib.net.obsr;

import com.beans.observables.ObservableValue;
import com.beans.observables.RegisteredListener;
import com.beans.observables.binding.ObservableBinding;
import com.beans.observables.binding.PropertyBindingController;
import com.beans.observables.listeners.ChangeEvent;
import com.beans.observables.listeners.ChangeListener;
import com.beans.observables.listeners.ObservableEventController;
import com.beans.observables.listeners.RegisteredListenerImpl;
import com.beans.observables.properties.ObservableProperty;
import com.beans.observables.properties.ObservablePropertyBase;
import com.flash3388.flashlib.net.obsr.Storage;
import com.flash3388.flashlib.net.obsr.StoragePath;
import com.flash3388.flashlib.net.obsr.Value;
import com.flash3388.flashlib.net.obsr.ValueProperty;
import com.notifier.Event;
import com.notifier.EventController;

import java.lang.ref.WeakReference;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class EntryValueObservableProperty extends ObservablePropertyBase<Value> implements ValueProperty {

    private final WeakReference<Storage> mStorage;
    private final StoragePath mPath;

    private EntryValueObservableProperty(Storage storage,
                                         StoragePath path,
                                         EventController eventController,
                                         Object bean,
                                         AtomicReference<ObservableValue<Value>> thisObservable) {
        super(bean,
                new CustomEventController(eventController, thisObservable),
                new DisabledBindingController());
        thisObservable.set(this);
        mStorage = new WeakReference<>(storage);
        mPath = path;
    }

    public EntryValueObservableProperty(Storage storage,
                                         StoragePath path,
                                         EventController eventController,
                                         Object bean) {
        this(storage, path, eventController, bean, new AtomicReference<>());
    }

    public void invokeChangeListener(Value oldValue, Value newValue) {
        fireValueChangedEvent(oldValue, newValue);
    }

    @Override
    protected void setInternalDirect(Value value) {
        Objects.requireNonNull(value, "value is null");

        Storage storage = getStorage();
        storage.setEntryValue(mPath, value);
    }

    @Override
    public void set(Value value) {
        setInternalDirect(value);
    }

    @Override
    public Value get() {
        Storage storage = getStorage();
        return storage.getEntryValue(mPath);
    }

    private Storage getStorage() {
        Storage storage = mStorage.get();
        if (storage == null) {
            throw new IllegalStateException("storage was garbage collected");
        }

        return storage;
    }

    private static class CustomEventController implements ObservableEventController<Value> {
        private final EventController mEventController;
        private final Predicate<Event> mEventPredicate;

        public CustomEventController(EventController eventController, AtomicReference<ObservableValue<Value>> thisObservable) {
            mEventController = eventController;
            mEventPredicate = new ListenerPredicate(thisObservable);
        }

        @Override
        public RegisteredListener addListener(ChangeListener<? super Value> listener) {
            mEventController.registerListener(listener, mEventPredicate);
            return new RegisteredListenerImpl(mEventController, listener);
        }

        @Override
        public void fire(ChangeEvent<Value> event) {
            mEventController.fire(event, ChangeEvent.class, ChangeListener.class, ChangeListener::onChange);
        }
    }

    private static class ListenerPredicate implements Predicate<Event> {

        private final AtomicReference<ObservableValue<Value>> mObservable;

        private ListenerPredicate(AtomicReference<ObservableValue<Value>> observable) {
            mObservable = observable;
        }

        @Override
        public boolean test(Event event) {
            return event instanceof ChangeEvent && ((ChangeEvent<?>)event).getObservableValue().equals(mObservable.get());
        }
    }

    private static class DisabledBindingController implements PropertyBindingController<Value> {

        @Override
        public boolean isBound() {
            return false;
        }

        @Override
        public void bind(ObservableValue<Value> observableValue, Consumer<ChangeEvent<Value>> onObservableValueChange) {
            throw new UnsupportedOperationException("cannot bind objectstorage entry");
        }

        @Override
        public Optional<ObservableBinding<Value>> unbind() {
            return Optional.empty();
        }

        @Override
        public Optional<ObservableBinding<Value>> getBinding() {
            return Optional.empty();
        }

        @Override
        public void bindBidirectional(ObservableProperty<Value> observableProperty, Consumer<ChangeEvent<Value>> onObservableValueChange) {
            throw new UnsupportedOperationException("cannot bind objectstorage entry");
        }
    }
}
