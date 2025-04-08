package com.clubfactory.platform.scheduler.core;

/**
 * server stop interface.
 */
public interface IStoppable {
  /**
   * Stop this service.
   * @param cause why stopping
   */
  void stop(String cause);

}