#version 330

#moj_import <plantz:paint_info.glsl>

uniform sampler2D Sampler0;

in vec2 texCoord0;
in vec4 vertexColor;
out vec4 fragColor;

float hash(vec2 p) {
    return fract(sin(dot(p, vec2(127.1, 311.7))) * 43758.5453);
}
float valueNoise(vec2 p) {
    vec2 i = floor(p);
    vec2 f = fract(p);
    float a = hash(i);
    float b = hash(i + vec2(1.0, 0.0));
    float c = hash(i + vec2(0.0, 1.0));
    float d = hash(i + vec2(1.0, 1.0));
    vec2 u = f * f * (3.0 - 2.0 * f);
    return mix(mix(a, b, u.x), mix(c, d, u.x), u.y);
}

void main() {
    vec4 base = texture(Sampler0, texCoord0);
    if (base.a < 0.1) discard;

    float scale    = max(NoiseScale, 0.001);
    float strength = clamp(NoiseStrength, 0.0, 8.0);

    float n = valueNoise(texCoord0 * scale);
    n = smoothstep(1.0 - strength, 1.0, n);

    float a = n * base.a * vertexColor.a;
    if (a < 0.01) discard;

    fragColor = vec4(vertexColor.rgb, a);
}