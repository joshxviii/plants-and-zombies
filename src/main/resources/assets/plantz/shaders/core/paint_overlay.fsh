#version 330

#moj_import <plantz:paint_info.glsl>

uniform sampler2D Sampler0;
uniform sampler2D Sampler1;

in vec2 texCoord0;
in vec4 vertexColor;

out vec4 fragColor;

void main() {
    vec4 base = texture(Sampler0, texCoord0);
    if (base.a < 0.1) discard;

    float strength = clamp(NoiseStrength, 0.0, 1.0);

    float n = texture(Sampler1, texCoord0).a;

    float paintAlpha = n * strength * vertexColor.a * base.a;
    if (paintAlpha < 0.01) discard;

    fragColor = vec4(vertexColor.rgb, paintAlpha);
}