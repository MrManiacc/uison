#vert
#version 330

in vec3 vertex;
in vec3 normal;
in vec2 uv;

out vec2 passUV;


uniform mat4 transformMatrix;
uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;

void main(void){
    gl_Position = projectionMatrix * viewMatrix * transformMatrix * vec4(vertex, 1.0);
    passUV = uv;
}

    #frag
    #version 330


out vec4 outColor;
in vec2 passUV;

uniform sampler2D diffuseMap;
void main(void){

//    outColor = vec4(1, 1, 1, 1.0);
    outColor = texture(diffuseMap, passUV);
    //    outColor = vec4(passColor, 0.0) + texture(spritesheet, passUV);
    //    outColor = vec4(passColor, 1.0);
}
