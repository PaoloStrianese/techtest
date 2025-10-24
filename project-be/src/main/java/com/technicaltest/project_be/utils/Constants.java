package com.technicaltest.project_be.utils;

import lombok.Getter;

public class Constants {

  public enum ReviewStatus {
    PROCESSED, REJECTED, QUEUED
  }

  @Getter
  public enum MessageStatus {
    SENT_ERROR("Errore durante l'invio del messaggio"),
    DELIVERED("Messaggio consegnato al consumatore"),
    BOOK_NOT_EXIST("Il Libro selezionato non Esiste"),
    PROCESSING_ERROR("Errore durante l'elaborazione del messaggio"),
    QUEUED("Messaggio accodato in attesa di elaborazione");

    private final String description;

    MessageStatus(String description) {
      this.description = description;
    }

    @Override
    public String toString() {
      return description;
    }
  }
}
