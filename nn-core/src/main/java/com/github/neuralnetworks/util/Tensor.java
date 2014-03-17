package com.github.neuralnetworks.util;

import java.io.Serializable;
import java.util.Arrays;
import java.util.stream.IntStream;

/**
 * N-dimensional tensor. For example 2-dim tensor is a matrix
 */
public class Tensor implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * tensor elements
     */
    private float[] elements;

    /**
     * dimension lengths for the tensor
     */
    private int[] dimensions;

    /**
     * sub-tensor start positions for each dimension
     */
    private int[] dimStart;

    /**
     * sub-tensor end positions for each dimension
     */
    private int[] dimEnd;

    private int[] dimMultiplicators;

    public Tensor(int... dimensions) {
	if (dimensions == null || dimensions.length == 0) {
	    throw new IllegalArgumentException("Please provide dimensions");
	}

	this.dimensions = dimensions;
	this.dimEnd = dimensions;
	this.dimStart = new int[dimensions.length];
	this.elements = new float[IntStream.of(dimensions).reduce(1, (a, b) -> a * b)];

	dimMultiplicators = new int[dimensions.length];
	IntStream.range(0, dimMultiplicators.length).forEach(i -> {
	    dimMultiplicators[i] = 1;
	    Arrays.stream(dimensions).skip(i + 1).limit(dimensions.length).forEach(j -> dimMultiplicators[i] *= j);
	});
    }

    public Tensor(float[] elements, int... dimensions) {
	if (dimensions == null || dimensions.length == 0) {
	    throw new IllegalArgumentException("Please provide dimensions");
	}
	
	this.dimensions = dimensions;
	this.dimEnd = dimensions;
	this.dimStart = new int[dimensions.length];
	this.elements = elements;
	
	dimMultiplicators = new int[dimensions.length];
	IntStream.range(0, dimMultiplicators.length).forEach(i -> {
	    dimMultiplicators[i] = 1;
	    Arrays.stream(dimensions).skip(i + 1).limit(dimensions.length).forEach(j -> dimMultiplicators[i] *= j);
	});
    }
    
    public Tensor(Tensor parent, int[] dimStart, int[] dimEnd) {
	this.dimensions = parent.dimensions;
	this.elements = parent.elements;
	this.dimMultiplicators = parent.dimMultiplicators;
	this.dimStart = dimStart;
	this.dimEnd = dimEnd;
    }

    public float get(int... d) {
	return elements[getIndex(d)];
    }

    public void set(float value, int... d) {
	elements[getIndex(d)] = value;
    }

    protected int getIndex(int... d) {
	if (d == null || d.length == 0 || d.length != dimensions.length) {
	    throw new IllegalArgumentException("Please provide indices");
	}

	int id = IntStream.range(0, dimensions.length).map(i -> {
	    if (d[i] + dimStart[i] > dimEnd[i]) {
		throw new IllegalArgumentException("Index out of range: " + i + " -> " + d[i] + "+" + dimStart[i] + " to " + dimStart[i]);
	    }

	    return (d[i] + dimStart[i]) * dimMultiplicators[i];
	}).sum();

	return id;
    }

    public float[] getElements() {
        return elements;
    }

    public int[] getDimensions() {
        return dimensions;
    }
}