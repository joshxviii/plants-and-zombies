#version 330

uniform sampler2D Sampler0;
uniform sampler2D Sampler1;

in vec2 texCoord0;
in vec4 vertexColor;

out vec4 fragColor;

void main() {
    vec4 base = texture(Sampler0, texCoord0);
    if (base.a < 0.1) discard;

    float n = texture(Sampler1, texCoord0).r;

    float paintAlpha = n * vertexColor.a * base.a;

    fragColor = vec4(vertexColor.rgb, paintAlpha);
}