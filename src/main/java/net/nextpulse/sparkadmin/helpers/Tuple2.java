package net.nextpulse.sparkadmin.helpers;

import lombok.Getter;

public class Tuple2<L, R> {
  @Getter
  private final L left;
  @Getter
  private final R right;

  public Tuple2(L left, R right) {
    this.left = left;
    this.right = right;
  }
}