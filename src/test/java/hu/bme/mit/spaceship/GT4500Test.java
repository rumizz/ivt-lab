package hu.bme.mit.spaceship;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

public class GT4500Test {

  private GT4500 ship;
  TorpedoStore primaryTorpedoStore;
  TorpedoStore secondaryTorpedoStore;

  @BeforeEach
  public void init() {
    primaryTorpedoStore = mock(TorpedoStore.class);
    secondaryTorpedoStore = mock(TorpedoStore.class);
    this.ship = new GT4500(primaryTorpedoStore, secondaryTorpedoStore);
  }

  @Test
  public void fireTorpedo_Single_Success() {
    // Arrange
    when(primaryTorpedoStore.fire(1)).thenReturn(true);
    when(secondaryTorpedoStore.fire(1)).thenReturn(true);
    // Act
    boolean result = ship.fireTorpedo(FiringMode.SINGLE);

    // Assert
    assertEquals(true, result);
  }

  @Test
  public void fireTorpedo_All_Success() {
    // Arrange
    when(primaryTorpedoStore.fire(1)).thenReturn(true);
    when(secondaryTorpedoStore.fire(1)).thenReturn(true);
    // Act
    boolean result = ship.fireTorpedo(FiringMode.ALL);
    // Assert
    assertEquals(true, result);
  }

  @Test
  public void fireTorpedo_Single_first_success() {
    // Arrange
    when(primaryTorpedoStore.isEmpty()).thenReturn(false);
    when(secondaryTorpedoStore.isEmpty()).thenReturn(false);

    when(primaryTorpedoStore.fire(1)).thenReturn(true);
    when(secondaryTorpedoStore.fire(1)).thenReturn(false);
    // Act
    boolean result = ship.fireTorpedo(FiringMode.SINGLE);
    // Assert
    assertEquals(true, result);
  }

  @Test
  public void fireTorpedo_Single_first_but_primary_empty_success() {
    // Arrange
    when(primaryTorpedoStore.isEmpty()).thenReturn(true);
    when(secondaryTorpedoStore.isEmpty()).thenReturn(false);

    when(primaryTorpedoStore.fire(1)).thenReturn(false);
    when(secondaryTorpedoStore.fire(1)).thenReturn(true);
    // Act
    boolean result = ship.fireTorpedo(FiringMode.SINGLE);
    // Assert
    assertEquals(true, result);
  }

  @Test
  public void fireTorpedo_Single_fail_no_retry() {
    when(primaryTorpedoStore.isEmpty()).thenReturn(false);
    when(secondaryTorpedoStore.isEmpty()).thenReturn(false);

    when(primaryTorpedoStore.fire(1)).thenReturn(false);
    when(secondaryTorpedoStore.fire(1)).thenThrow(new RuntimeException("Should not be called"));
    // Act
    assertDoesNotThrow(() -> {
      boolean result = ship.fireTorpedo(FiringMode.SINGLE);
      assertEquals(false, result);
    });
  }

  @Test
  public void fireTorpedo_All_fail() {
    // Arrange
    when(primaryTorpedoStore.isEmpty()).thenReturn(false);
    when(secondaryTorpedoStore.isEmpty()).thenReturn(false);

    when(primaryTorpedoStore.fire(1)).thenReturn(false);
    when(secondaryTorpedoStore.fire(1)).thenReturn(false);
    // Act
    boolean result = ship.fireTorpedo(FiringMode.ALL);
    // Assert
    assertEquals(false, result);
  }

  @Test
  public void fireTorpedo_All_both_are_empty_fail() {
    // Arrange
    when(primaryTorpedoStore.isEmpty()).thenReturn(true);
    when(secondaryTorpedoStore.isEmpty()).thenReturn(true);

    when(primaryTorpedoStore.fire(1)).thenThrow(new RuntimeException("Should not be called"));
    when(secondaryTorpedoStore.fire(1)).thenThrow(new RuntimeException("Should not be called"));
    // Act
    assertDoesNotThrow(() -> {
      boolean result = ship.fireTorpedo(FiringMode.ALL);
      // Assert
      assertEquals(false, result);
    });
  }

  @Test void fireTorpedo_All_success_if_one_succeeds() {
    // Arrange
    when(primaryTorpedoStore.isEmpty()).thenReturn(false);
    when(secondaryTorpedoStore.isEmpty()).thenReturn(false);
      
    when(primaryTorpedoStore.fire(1)).thenReturn(true);
    when(secondaryTorpedoStore.fire(1)).thenReturn(false);

    // Act
    boolean result = ship.fireTorpedo(FiringMode.ALL);
    // Assert
    assertEquals(true, result);

    //
    when(primaryTorpedoStore.fire(1)).thenReturn(false);
    when(secondaryTorpedoStore.fire(1)).thenReturn(true);

    // Act
    result = ship.fireTorpedo(FiringMode.ALL);
    // Assert
    assertEquals(true, result);

    //
    when(primaryTorpedoStore.fire(1)).thenReturn(false);
    when(secondaryTorpedoStore.fire(1)).thenReturn(false);

    // Act
    result = ship.fireTorpedo(FiringMode.ALL);
    // Assert
    assertEquals(false, result);
  }

  @Test
  void fireTorpedo_Single_wasPrimaryFiredLast() {
    
    // Setup
    when(primaryTorpedoStore.isEmpty()).thenReturn(false);
    when(secondaryTorpedoStore.isEmpty()).thenReturn(true);
      
    when(primaryTorpedoStore.fire(1)).thenReturn(true);
    when(secondaryTorpedoStore.fire(1)).thenReturn(true);

    // Set wasPrimaryFiredLast to true
    ship.fireTorpedo(FiringMode.ALL);

    // second not empty, primary should not be called
    when(primaryTorpedoStore.fire(1)).thenThrow(new RuntimeException("Should not be called"));
    when(secondaryTorpedoStore.fire(1)).thenReturn(false);

    assertDoesNotThrow(() -> {
      boolean result = ship.fireTorpedo(FiringMode.ALL);
      // Assert
      assertEquals(true, result);
    });

    // second is empty, primary should be called
    when(primaryTorpedoStore.isEmpty()).thenReturn(false);
    when(secondaryTorpedoStore.isEmpty()).thenReturn(true);
    when(primaryTorpedoStore.fire(1)).thenReturn(false);
    when(secondaryTorpedoStore.fire(1)).thenThrow(new RuntimeException("Should not be called"));

    assertDoesNotThrow(() -> {
      boolean result = ship.fireTorpedo(FiringMode.ALL);
      // Assert
      assertEquals(true, result);
    });

    // both are empty, none should be called
    when(primaryTorpedoStore.fire(1)).thenThrow(new RuntimeException("Should not be called"));
    when(secondaryTorpedoStore.fire(1)).thenThrow(new RuntimeException("Should not be called"));

    assertDoesNotThrow(() -> {
      boolean result = ship.fireTorpedo(FiringMode.ALL);
      // Assert
      assertEquals(false, result);
    });
  }

}
