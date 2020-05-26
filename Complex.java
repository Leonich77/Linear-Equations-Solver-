package solver;

import java.util.Objects;

public class Complex {

    private final float re;   // the real part
    private final float im;   // the imaginary part

    // create a new object with the given real and imaginary parts
    public Complex(float real, float imag) {
        re = real;
        im = imag;
    }

    // return a string representation of the invoking Complex object
    public String toString() {
        if (im == 0) return re + "";
        if (re == 0) return im + "i";
        if (im <  0) return re + "-" + (-im) + "i";
        return re + "+" + im + "i";
    }
    public boolean isZero() {
        return equals(new Complex(0f, 0));
    }

    // return abs/modulus/magnitude
    public double abs() { return Math.hypot(re, im); }

    // return angle/phase/argument, normalized to be between -pi and pi
    public double phase() { return Math.atan2(im, re); }

    // return a new Complex object whose value is (this + b)
    public Complex plus(Complex b) {
        Complex a = this;             // invoking object
        float real = a.re + b.re;
        float imag = a.im + b.im;
        return new Complex(real, imag);
    }

    // return a new Complex object whose value is (this - b)
    public Complex minus(Complex b) {
        Complex a = this;
        float real = a.re - b.re;
        float imag = a.im - b.im;
        return new Complex(real, imag);
    }

    // return a new Complex object whose value is (this * b)
    public Complex times(Complex b) {
        Complex a = this;
        float real = a.re * b.re - a.im * b.im;
        float imag = a.re * b.im + a.im * b.re;
        return new Complex(real, imag);
    }

    // return a new object whose value is (this * alpha)
    public Complex scale(float alpha) { return new Complex(alpha * re, alpha * im); }

    // return a new Complex object whose value is the conjugate of this
    public Complex conjugate() { return new Complex(re, -im); }

    // return a new Complex object whose value is the reciprocal of this
    public Complex reciprocal() {
        float scale = re*re + im*im;
        return new Complex(re / scale, -im / scale);
    }

    // return the real or imaginary part
    public double re() { return re; }
    public double im() { return im; }

    // return a / b
    public Complex divides(Complex b) {
        Complex a = this;
        return a.times(b.reciprocal());
    }

    // return a new Complex object whose value is the complex exponential of this
    public Complex exp() { return new Complex((float)(Math.exp(re) * Math.cos(im)), (float)(Math.exp(re) * Math.sin(im))); }

    // return a new Complex object whose value is the complex sine of this
    public Complex sin() { return new Complex((float)(Math.sin(re) * Math.cosh(im)), (float)(Math.cos(re) * Math.sinh(im))); }

    // return a new Complex object whose value is the complex cosine of this
    public Complex cos() { return new Complex((float)(Math.cos(re) * Math.cosh(im)), (float)(-Math.sin(re) * Math.sinh(im))); }

    // return a new Complex object whose value is the complex tangent of this
    public Complex tan() { return sin().divides(cos()); }

    // a static version of plus
    public static Complex plus(Complex a, Complex b) {
        float real = a.re + b.re;
        float imag = a.im + b.im;
        return new Complex(real, imag);
    }

    @Override
    public boolean equals(Object x) {
        if (x == null) return false;
        if (this.getClass() != x.getClass()) return false;
        Complex that = (Complex) x;
        return (this.re == that.re) && (this.im == that.im);
    }

    @Override
    public int hashCode() {
        return Objects.hash(re, im);
    }

    //TODO: refactor by regex
    public static Complex parseComplex(String s)
    {
        s = s.replaceAll(" ","");
        Complex parsed = null;
        if(s.contains("+") || (s.contains("-") && s.lastIndexOf('-') > 0)) {
            String re = "";
            String im = "";
            s = s.replaceAll("i","");
            s = s.replaceAll("I","");
            if (s.indexOf('+') > 0) {
                re = s.substring(0,s.indexOf('+'));
                im = s.substring(s.indexOf('+')+1, s.length());
                if (im.equals("")) {
                    im = "1";
                }
                parsed = new Complex(Float.parseFloat(re), Float.parseFloat(im));
            }
            else if(s.lastIndexOf('-') > 0) {
                re = s.substring(0,s.lastIndexOf('-'));
                im = s.substring(s.lastIndexOf('-')+1,s.length());
                if (im.equals("")) {
                    im = "1";
                }
                parsed = new Complex(Float.parseFloat(re), -Float.parseFloat(im));
            }
        }
        else {
            if(s.endsWith("i") || s.endsWith("I")) {    // Pure imaginary number
                s = s.replaceAll("i","");
                s = s.replaceAll("I","");
                boolean minus = false;
                if (s.startsWith("-")) {
                    minus = true;
                    s = s.replaceAll("-", "");
                }
                if (s.equals("")) {
                    if (minus) {
                        parsed = new Complex(0, -1);
                    } else {
                        parsed = new Complex(0, 1);
                    }
                } else {
                    if (minus) {
                        parsed = new Complex(0, -Float.parseFloat(s));
                    } else {
                        parsed = new Complex(0, Float.parseFloat(s));
                    }
                }
            } else {                                    // Pure real number
                if (s.equals("")) {
                    parsed = new Complex(0,0);
                } else {
                    parsed = new Complex(Float.parseFloat(s),0);
                }
            }
        }
        return parsed;
    }
}
