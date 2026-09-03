#version 330

#moj_import <minecraft:fog.glsl>
#moj_import <minecraft:matrix.glsl>
#moj_import <minecraft:globals.glsl>

uniform sampler2D Sampler0;
uniform sampler2D Sampler1;

in vec2 texCoord0;
in vec4 vertexColor;
in float sphericalVertexDistance;
in float cylindricalVertexDistance;

out vec4 fragColor;

float hash(vec2 p) {
    return fract(sin(dot(p, vec2(127.1, 311.7))) * 43758.5453);
}

float noise(vec2 p) {
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
    vec2 uv = texCoord0 * 2.0 - 1.0;
    float r = length(uv);
    float angle = atan(uv.y, uv.x);

    float t = GameTime * 1000.0;

    vec2 dir = vec2(cos(angle), sin(angle));

    float swirlFast = angle + (1.0 - r) * (1.8 + sin(t * 0.7) * 0.4) + t * 0.35;
    float rippleInner = sin(r * 14.0 - t * 3.0) * 0.04 * (1.0 - r);

    float swirlSlow = angle + (1.0 - r) * 0.9 + t * 0.12;
    float edgeFactor = smoothstep(0.45, 0.95, r);

    float slow1 = swirlSlow;
    float slow2 = swirlSlow * 2.0;
    float slow3 = swirlSlow * 3.0;
    float slow4 = swirlSlow * 4.0;

    float bentPhase = r * 18.0 - slow3 - t * 1.1;
    float rippleOuter = sin(bentPhase) * 0.035 * edgeFactor;
    rippleOuter += sin(r * 9.0 + slow1 - t * 0.55) * 0.02 * edgeFactor;
    rippleOuter += sin(r * 11.0 - slow2 - t * 0.8) * 0.012 * edgeFactor;

    float rWarp = r + rippleOuter;

    float radius = 0.92;
    float circle = 1.0 - smoothstep(radius - 0.10, radius, rWarp);
    if (circle < 0.001) discard;

    float ripple = rippleInner + rippleOuter * 0.35;
    vec2 warped = vec2(cos(swirlFast), sin(swirlFast)) * (r + ripple);
    vec2 tuv = warped * 0.5 + 0.5;

    vec4 back = texture(Sampler0, tuv + vec2(noise(tuv * 6.0 + t) * 0.03));
    vec4 front = texture(Sampler1, tuv * 1.4 + vec2(t * 0.05, -t * 0.03));

    float n = noise(warped * 5.0 + t * 0.5);
    float energy = smoothstep(0.35, 0.85, n) * (1.0 - r);

    float rim = smoothstep(0.50, 0.90, rWarp) * smoothstep(1.02, 0.78, rWarp);

    float rimWave = 0.5 + 0.5 * sin(slow4 + t * 0.8);
    rimWave = mix(1.0, rimWave, edgeFactor);

    float rimBend = 0.5 + 0.5 * sin(r * 12.0 - slow2 - t * 0.85);
    rim *= pow(rim * rimWave * mix(1.0, rimBend, 0.65), 1.15);

    vec3 tint = vertexColor.rgb;
    vec3 core = mix(back.rgb, front.rgb, 0.45 + energy * 0.35);
    core *= tint * (0.7 + energy * 1.2);
    core += tint * rim * 1.55;
    core += vec3(1.0) * energy * 0.25;

    float rimNoise = noise(dir * (2.0 + r * 4.0) + vec2(t * 0.15));
    core += tint * rim * rimNoise * 0.45;

    float alpha = circle * vertexColor.a * (0.55 + energy * 0.35 + rim * 0.5);
    alpha = clamp(alpha, 0.0, 1.0);

    vec4 color = vec4(core, alpha);
    fragColor = apply_fog(
            color,
            sphericalVertexDistance,
            cylindricalVertexDistance,
            FogEnvironmentalStart,
            FogEnvironmentalEnd,
            FogRenderDistanceStart,
            FogRenderDistanceEnd,
            FogColor
    );
}