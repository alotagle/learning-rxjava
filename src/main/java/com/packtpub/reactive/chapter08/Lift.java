package com.packtpub.reactive.chapter08;

import java.nio.file.Path;
import java.nio.file.Paths;

import rx.Observable;
import rx.Observable.Operator;
import rx.Subscriber;

import com.packtpub.reactive.common.CreateObservable;
import com.packtpub.reactive.common.Helpers;
import com.packtpub.reactive.common.Program;

/**
 * Demonstrates implementing values with indices using lift and the custom operator {@link Indexed}.
 * 
 * @author meddle
 */
public class Lift implements Program {

	public static class Pair<L, R> {
		final L left;
		final R right;
		
		public Pair(L left, R right) {
			this.left = left;
			this.right = right;
		}
		
		public L getLeft() {
			return left;
		}
		
		public R getRight() {
			return right;
		}
		
		public Pair<L, R> setLeft(L newLeft) {
			return new Pair<L, R>(newLeft, this.right);
		}

		public Pair<L, R> setRight(R newRight) {
			return new Pair<L, R>(this.left, newRight);
		}
		
		@Override
		public String toString() {
			return String.format("%s : %s", this.left, this.right);
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((left == null) ? 0 : left.hashCode());
			result = prime * result + ((right == null) ? 0 : right.hashCode());
			return result;
		}

		@SuppressWarnings("rawtypes")
		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (!(obj instanceof Pair)) {
				return false;
			}
			Pair other = (Pair) obj;
			if (left == null) {
				if (other.left != null) {
					return false;
				}
			} else if (!left.equals(other.left)) {
				return false;
			}
			if (right == null) {
				if (other.right != null) {
					return false;
				}
			} else if (!right.equals(other.right)) {
				return false;
			}
			return true;
		}
		
		
	}
	
	public static class Indexed<T> implements Operator<Pair<Long, T>, T> {
		private long index;
		
		public Indexed() {
			this(0L);
		}

		public Indexed(long initial) {
			this.index = initial;
		}

		@Override
		public Subscriber<? super T> call(Subscriber<? super Pair<Long, T>> s) {
			return new Subscriber<T>() {
				@Override
				public void onCompleted() {
					s.onCompleted();
				}

				@Override
				public void onError(Throwable e) {
					s.onError(e);
				}

				@Override
				public void onNext(T t) {
					s.onNext(new Pair<Long, T>(index++, t));
				}
			};
		}
	}
	
	@Override
	public String name() {
		return "Example of using Observable#lift for executing custom operators";
	}

	@Override
	public int chapter() {
		return 8;
	}

	@Override
	public void run() {
		Path path = Paths.get("src", "main", "resources", "letters.txt");
		Observable<?> indexedStrings = CreateObservable
				.from(path)
				.lift(new Indexed<String>(1L));
		
		Helpers.subscribePrint(indexedStrings, "Indexed strings");
	}
	
	public static void main(String[] args) {
		new Lift().run();
	}

}
