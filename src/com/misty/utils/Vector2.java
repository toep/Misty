package com.misty.utils;

import java.awt.*;

public class Vector2 {
    public float x, y;

    public Vector2(float a, float b) {
        this.x = a;
        this.y = b;
    }

    public Vector2(Point p) {
        this.x = p.x;
        this.y = p.y;
    }

    public Vector2 normalize() {
        float mag = magnitude();
        return new Vector2(x/mag, y/mag);
    }

    public Vector2 sub(Vector2 o) {
        return new Vector2(x - o.x, y - o.y);
    }

    public float magnitude() {
        return (float) Math.sqrt(x * x + y * y);
    }

    public float dot(Vector2 v) {
        return x * v.x + y * v.y;
    }

    public float cross(Vector2 v) {
        return this.x*v.y-v.x*this.y;
    }

    public float dst(Vector2 v) {
        return (float) Math.sqrt((this.x-v.x)*(this.x-v.x) + (this.y-v.y)*(this.y-v.y));
    }

    public Vector2 mul(float i) {
        return new Vector2(x * i, y * i);
    }

    public Vector2 add(Vector2 v) { return new Vector2(x + v.x, y+v.y);}

    public float angleBetween(Vector2 other) { return (float) (Math.acos(this.dot(other))/(this.magnitude() * other.magnitude()));}

    public String toString() {

        return "<" + x + ", " + y + ">";
    }
}
