varying vec3 varyingColour;

void main() {
    gl_FragColor = vec4(varyingColour, 1);
}