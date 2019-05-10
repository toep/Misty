package com.misty.utils;

import java.awt.*;

public class Vector3 {
    public float x, y, z;

    public Vector3(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3 normalize() {
        float mag = magnitude();
        return new Vector3(x/mag, y/mag, z/mag);
    }

    public Vector3 sub(Vector3 o) {
        return new Vector3(x - o.x, y - o.y, z - o.z);
    }

    public float magnitude() {
        return (float) Math.sqrt(x * x + y * y + z * z);
    }

    public float dot(Vector3 v) {
        return x * v.x + y * v.y + z * v.z;
    }

    public Vector3 cross(Vector3 v) {
        return new Vector3(this.y*v.z-this.z*v.y, this.z*v.x - this.x*v.z, this.x*v.y - this.y*v.x);
    }

    public float dst(Vector3 v) {
        return (float) Math.sqrt((this.x-v.x)*(this.x-v.x) + (this.y-v.y)*(this.y-v.y) + (this.z-v.z)*(this.z-v.z));
    }

    public Vector3 mul(float i) {
        return new Vector3(x * i, y * i, z*i);
    }

    public Vector3 add(Vector3 v) { return new Vector3(x + v.x, y+v.y, z + v.z);}

    public float angleBetween(Vector3 other) { return (float) (Math.acos(this.dot(other))/(this.magnitude() * other.magnitude()));}

    public String toString() {

        return "<" + x + ", " + y + ", " + z + ">";
    }
}
