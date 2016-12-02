package net.nextpulse.sparkadmin.views;

import lombok.Data;

/**
 * @author yholkamp
 */
@Data
public class EditPost {

  private boolean success;
  private String errorMessage;

  public EditPost(boolean success, String errorMessage) {
    this.success = success;
    this.errorMessage = errorMessage;
  }

}
