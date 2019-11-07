#vert
#version 330

in vec3 vertex;
in vec3 uvID;
out float outID;

//out vec2 passUV;


uniform mat4 projectionMatrix;
uniform mat4 modelMatrix;

void main(void){
    gl_Position = projectionMatrix * modelMatrix * vec4(vertex, 1.0);
    outID = uvID.x;
}
    #frag
    #version 330


out vec4 outColor;
//in vec2 passUV;
in float outID;

uniform sampler2D diffuseMap;
void main(void){
    if (outID >= 0 && outID < 1){
        outColor = vec4(1, 0, 0, 1.0);
    } else if (outID >= 1 && outID < 2){
        outColor = vec4(0, 1, 0, 1.0);
    } else if (outID >= 2 && outID < 3){
        outColor = vec4(0, 0, 1, 1.0);
    } else if (outID >= 3 && outID < 4){
        outColor = vec4(1, 1, 0, 1.0);
    }
    //    outColor = texture(diffuseMap, passUV);
    //    outColor = vec4(passColor, 0.0) + texture(spritesheet, passUV);
    //    outColor = vec4(passColor, 1.0);
}
