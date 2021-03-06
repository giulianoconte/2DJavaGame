#version 330 core

layout (location = 0) out vec4 color;

in DATA
{
	vec2 tc;
	vec3 position; //position of pixel being shaded
} fs_in;

uniform vec2 player;
uniform vec2 mouse;
uniform int currentRoom;
uniform int lighting;
uniform int alpha;
uniform sampler2D tex;

void main()
{
	float M_PI = 3.14159265358;
	
	color = texture(tex, fs_in.tc); //the color at this texture coordinate
	color = color.xyzw; //color swapping
	//if texture coordinate is transparent, stop shading this pixel with this texture
	if (color.w < 1.0) discard;
	
	vec3 mata = color.xyz; //coefficient of reflectance for ambient lighting
	vec3 matd = color.xyz; //coefficient of reflectance for diffuse lighting
	vec3 mats = color.xyz; //coefficient of reflectance for specular lighting
	
	vec4 temp = color; //copy of color, we can modify and not lose original color if we decide to scrap
	float lightIntensity = 1.0;
	vec3 lightColor = vec3(1.0, 1.0, 1.0);
	float radAtten = 4.0; //how far the diffuse light reaches
	float sightDistance = radAtten + 1.0; //how far objects are visible
	float alphaMod = 1.0;
	float distFromPlayer = (length(player - fs_in.position.xy));	
	
	vec2 v1 = vec2(mouse.xy - player.xy);
	vec2 v2 = (fs_in.position.xy - player.xy);
	float coneAngle = M_PI / 5; //aperture of the spotlight cone
	float lightAngleCos = cos(coneAngle / 2);
	
	float rho = dot(normalize(v2), normalize(v1));
	float dif = 1.0 - lightAngleCos;
	float angAtten = clamp((rho - lightAngleCos) / dif, 0.0, 1.0); //angular attenuation from question post in <http://stackoverflow.com/questions/17487055/spotlight-angular-attenuation-causes-sharp-edges-when-angle-90>
	
	//if the player is in this room
	if (currentRoom == 1) {
		//ambient light
		lightIntensity = 0.05;
		lightColor = vec3(1.0, 1.0, 1.0);
		temp.r = mata.x * lightIntensity * lightColor.x;
		temp.g = mata.y * lightIntensity * lightColor.y;
		temp.b = mata.z * lightIntensity * lightColor.z;
		float radiation = 0.0;
		
		//spot light with radial attenuation
		lightIntensity = 0.0;
		lightColor = vec3(100, 92, 70); //orange color
		lightColor = normalize(lightColor.xyz);
		radiation = -(lightIntensity / radAtten) * distFromPlayer + lightIntensity; //radial atten: see <https://www.desmos.com/calculator/vkg52rekkk> for graph
		if (radiation < 0) radiation = 0;
		temp.r += matd.x * radiation * angAtten * lightColor.x;
		temp.g += matd.y * radiation * angAtten * lightColor.y;
		temp.b += matd.z * radiation * angAtten * lightColor.z;
		
		//spot light with radial attenuation that reaches further based on angAtten, gives it tighter curve at the end
		lightIntensity = 6.0;
		lightColor = vec3(100, 92, 70); //orange color
		lightColor = normalize(lightColor.xyz);
		radiation = -(lightIntensity / (0.7 * angAtten + radAtten)) * distFromPlayer + lightIntensity; //radial atten: see <https://www.desmos.com/calculator/vkg52rekkk> for graph
		if (radiation < 0) radiation = 0;
		temp.r += matd.x * radiation * angAtten * lightColor.x;
		temp.g += matd.y * radiation * angAtten * lightColor.y;
		temp.b += matd.z * radiation * angAtten * lightColor.z;

		//small diffuse light around the player
		lightIntensity = 1.0;
		lightColor = vec3(100, 100, 100); //white color
		lightColor = normalize(lightColor.xyz);
		radiation = -(lightIntensity / 2.3) * distFromPlayer + lightIntensity; //radial atten: see <https://www.desmos.com/calculator/vkg52rekkk> for graph
		if (radiation < 0) radiation = 0;
		temp.r += matd.x * radiation * lightColor.x;
		temp.g += matd.y * radiation * lightColor.y;
		temp.b += matd.z * radiation * lightColor.z;
		
		//field of view
		if (alpha == 0) {
			if (distFromPlayer < 3.3) distFromPlayer = 3.3;
			alphaMod = 1.0 * pow((distFromPlayer - (sightDistance - 2)), -2);
		}
		if (alphaMod < 0.0) alphaMod = 0.0;
		if (alphaMod >= 1.0) alphaMod = 1.0;
		temp.w *= alphaMod;
	}
	if (currentRoom == 0) {
		temp = color.xyzw;
		alphaMod = 0.15;
		temp.w *= alphaMod;
	}
	
	//if lighting is enabled, apply shaders
	if (lighting == 1) color = temp.xyzw; //swap swizzling variables for color swapping
	
	//1-pass blur, reorganize this!
	if (currentRoom == 0) {
		float resolution = 64.0;
		resolution = 512;
		float step = 1/resolution;
		vec2 tc = fs_in.tc;
		int radius_b = 3;
		int radius_s = 1;
		
		//blur
		/*
		vec2 tc_b = vec2(0.0, 0.0);
		vec4 c_b = vec4(0.0, 0.0, 0.0, 0.0);
		for (int j = -radius_b; j <= radius_b; j++) {		//loop y pixel offset
			for (int i = -radius_b; i <= radius_b; i++) {   //loop x pixel offset
				tc_b = vec2(tc.x + (i*step), tc.y + (j*step));
				c_b += texture(tex, tc_b);
			}
		}
		c_b = c_b/pow((radius_b*2 + 1), 2);
		temp = c_b;
		*/
		//sodel edge detection x
		
		
		vec2 tc_s = vec2(0.0, 0.0);
		vec4 c_s = vec4(0.0, 0.0, 0.0, 0.0);
		float grey = 0.0;
		vec4 c_grey = vec4(0.0, 0.0, 0.0, 0.0);
		float gx = 0.0;
		float gy = 0.0;
		int x_mult = 0;
		int y_mult = 0;
		int mult = 0;
		for (int j = -1; j <= 1; j++) {		//loop y pixel offset
			for (int i = -1; i <= 1; i++) {   //loop x pixel offset
				tc_s = vec2(tc.x + (i*step), tc.y + (j*step));
				c_s = texture(tex, tc_s);
				grey = (c_s.x + c_s.y + c_s.z)/3;
    			c_grey = vec4(grey, grey, grey, c_s.w);
    			
    			//sobel kernel
    			
    			if (i == -1 && j == 0) x_mult = 2;
    			else if (i == -1) x_mult = 1;
    			if (i == 0) x_mult = 0;
    			if (i == 1 && j == 0) x_mult = -2;
    			else if (i == 1) x_mult = -1;
    			if (j == -1 && i == 0) y_mult = 2;
    			else if (j == -1) y_mult = 1;
    			if (j == 0) y_mult = 0;
    			if (j == 1 && i == 0) y_mult = -2;
    			else if (j == 1) y_mult = -1;
    			/*
    			//laplacian kernel
    			if (i == -1 && j == 0) x_mult = -1;
    			if (i == 0 && j == 0) x_mult = 4;
    			if (i == 1 && j == 0) x_mult = -1;
    			if (j == -1 && i == 0) x_mult = -1;
    			if (j == 1 && i == 0) x_mult = -1;
    			*/
				gx += grey*x_mult;
				gy += grey*y_mult;
				if (i == 0 && j == 0) temp = c_grey;
			}
		}
		float g = sqrt(pow(gx, 2.0) + pow(gy, 2.0));
		vec4 c_g = vec4(g, g, g, 1);
		temp = c_g;
		
		
		//greyscale
		/*
    	float grey = (color.x + color.y + color.z)/3;
    	vec4 c_grey = vec4(grey, grey, grey, color.w);
    	temp = c_grey;
    	*/
		if (lighting == 1) {
			alphaMod = 0.15;
			temp.w *= alphaMod;
		}
    	
		color = temp;
	}
}