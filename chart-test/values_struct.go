package test

// https://zhwt.github.io/yaml-to-go/
type Values struct {
	Deployment struct {
		Replicas int    `yaml:"replicas"`
		Image    string `yaml:"image"`
		Requests struct {
			CPU    string `yaml:"cpu"`
			Memory string `yaml:"memory"`
		} `yaml:"requests"`
		Limits struct {
			CPU    string `yaml:"cpu"`
			Memory string `yaml:"memory"`
		} `yaml:"limits"`
	} `yaml:"deployment"`
	Service struct {
		Type string `yaml:"type"`
	} `yaml:"service"`
	Ingress struct {
		EnableCors           bool   `yaml:"enable_cors"`
		CorsAllowOrigin      string `yaml:"cors_allow_origin"`
		CorsAllowCredentials string `yaml:"cors_allow_credentials"`
		SecretName           string `yaml:"secretName"`
		Hostname             string `yaml:"hostname"`
	} `yaml:"ingress"`
}
