package edu.flash3388.flashlib.util.beans.observable;

import java.util.Arrays;

import edu.flash3388.flashlib.util.FlashUtil;
import edu.flash3388.flashlib.util.beans.ChangeListener;

public abstract class ObservablePropertyHelper<T> {

    public static <T> ObservablePropertyHelper<T> addListener(ObservablePropertyHelper<T> helper, ObservableValue<T> observable, ChangeListener<? super T> listener) {
        if ((observable == null) || (listener == null)) 
            throw new NullPointerException();
        return (helper == null)? new Generic<T>(observable, listener) : helper.addListener(listener);
    }

    public static <T> ObservablePropertyHelper<T> removeListener(ObservablePropertyHelper<T> helper, ChangeListener<? super T> listener) {
        if (listener == null) 
            throw new NullPointerException();
        return (helper == null)? null : helper.removeListener(listener);
    }

    public static <T> void fireValueChangedEvent(ObservablePropertyHelper<T> helper) {
        if (helper != null) 
            helper.fireValueChangedEvent();
    }
	
	
	protected final ObservableValue<T> observable;
	
	protected ObservablePropertyHelper(ObservableValue<T> observable){
		this.observable = observable;
	}
	
	protected abstract ObservablePropertyHelper<T> addListener(ChangeListener<? super T> listener);
	protected abstract ObservablePropertyHelper<T> removeListener(ChangeListener<? super T> listener);
	
	protected abstract void fireValueChangedEvent();
	
	
	private static class Generic<T> extends ObservablePropertyHelper<T>{

		private ChangeListener<? super T>[] changeListeners;
		private int changeListeneresCount;
		private T currentValue;
		
		@SuppressWarnings("unchecked")
		private Generic(ObservableValue<T> observable, ChangeListener<? super T> changeListener) {
			super(observable);
			this.changeListeners = new ChangeListener[]{changeListener};
			this.changeListeneresCount = 1;
			this.currentValue = observable.getValue();
		}

		@Override
		protected ObservablePropertyHelper<T> addListener(ChangeListener<? super T> listener) {
			final int oldCapacity = changeListeners.length;
			int newCapacity = (changeListeneresCount < oldCapacity)? oldCapacity : (oldCapacity * 3) / 2 + 1;
			if(newCapacity != oldCapacity)
				changeListeners = Arrays.copyOf(changeListeners, newCapacity);
			changeListeners[changeListeneresCount++] = listener;
			return this;
		}
		@Override
		protected ObservablePropertyHelper<T> removeListener(ChangeListener<? super T> listener) {
			int idx = FlashUtil.indexOf(changeListeners, 0, changeListeneresCount, listener);
			if(idx >= 0){
				changeListeners[idx] = null;
				FlashUtil.shiftArrayL(changeListeners, idx, changeListeneresCount);
				--changeListeneresCount;
				
				if(changeListeneresCount < (changeListeners.length / 3 * 2))
					changeListeners = Arrays.copyOf(changeListeners, changeListeneresCount);
			}
			return this;
		}
		@Override
		protected void fireValueChangedEvent() {
			final ChangeListener<? super T>[] listeners = changeListeners;
			final int listenersCount = changeListeneresCount;
			
			if(listenersCount > 0){
				final T oldValue = currentValue;
				currentValue = observable.getValue();
				final boolean changed = (currentValue == null)? (oldValue != null) : !currentValue.equals(oldValue);
				if(changed){
					for (int i = 0; i < listeners.length; i++) {
						try {
							listeners[i].changed(observable, oldValue, currentValue);
	                    } catch (Exception e) {
	                        Thread.currentThread().getUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
	                    }
					}
				}
			}
		}
	}
}
