package net.nextpulse.sparkadmin.helpers;

public class Tuple2<L, R> {
  private final L left;
  private final R right;

  public Tuple2(L left, R right) {
    this.left = left;
    this.right = right;
  }

  public L getLeft() {
    return left;
  }

  public R getRight() {
    return right;
  }
}