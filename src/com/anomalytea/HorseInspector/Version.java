package com.anomalytea.HorseInspector;

import static java.lang.Integer.parseInt;

public class Version {
  private final int major;
  private final int minor;
  private final int patch;

  public Version(String s) {
    if (!s.matches("[0-9]+\\.[0-9]+\\.[0-9]+")) {
      throw new IllegalArgumentException("Value is not a valid version number. Must match format: x.y.z");
    } else {
      String[] verArray = s.split("\\.");
      this.major = parseInt(verArray[0]);
      this.minor = parseInt(verArray[1]);
      this.patch = parseInt(verArray[2]);
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Version)) {
      return false;
    }
    Version v = (Version) o;
    return this.major == v.major
        && this.minor == v.minor
        && this.patch == v.patch;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 31 * hash + this.major;
    hash = 31 * hash + this.minor;
    hash = 31 * hash + this.patch;
    return hash;
  }

  @Override
  public String toString() {
    return this.major + "." + this.minor + "." + this.patch;
  }

  public boolean isGreaterThan(Version v) {
    if (this.major > v.major) {
      return true;
    } else if (this.major < v.major) {
      return false;
    } else if (this.minor > v.minor) {
      return true;
    } else if (this.minor < v.minor) {
      return false;
    } else {
      return this.patch > v.patch;
    }
  }

  public boolean isLessThan(Version v) {
    return v.isGreaterThan(this);
  }

  public int getMajor() {
    return this.major;
  }

  public int getMinor() {
    return this.minor;
  }

  public int getPatch() {
    return this.patch;
  }

}
