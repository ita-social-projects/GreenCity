package test

import (
	"fmt"
	"os"
	"strings"
	"testing"

	"github.com/Skyere/helm-testing/helper"
	"github.com/Skyere/helm-testing/ingress"
	"github.com/Skyere/helm-testing/service"
	"github.com/gruntwork-io/terratest/modules/helm"
	"github.com/gruntwork-io/terratest/modules/k8s"
	"github.com/gruntwork-io/terratest/modules/random"
)

var namespaceName string = "test"
var kubectlOptions *k8s.KubectlOptions = k8s.NewKubectlOptions("", "", namespaceName)
var image string = os.Getenv("dockerRepoName") + ":test-" + os.Getenv("GITHUB_SHA_SHORT")

var options *helm.Options = &helm.Options{
	KubectlOptions: kubectlOptions,
	ValuesFiles:    []string{"valuesTest.yaml"},
	SetValues: map[string]string{
		"deployment.image": image,
		"environment": "test",
	},
}

var releaseName string = fmt.Sprintf(
	"greencity%s",
	strings.ToLower(random.UniqueId()),
)

func TestGreencity(t *testing.T) {

	chartPath := "../greencity-chart"
	serviceName := "greencity-service"
	ingressName := "greencity-ingress"
	// siteUrl := "https://greencity-chat-test.test-greencity.ga/swagger-ui.html"

	// Destroy release after testing
	defer helper.Destroy(t, releaseName, options)

	helper.Deploy(t, releaseName, chartPath, options)

	// Check if deployment is successful
	if !helper.WasDeploySuccessful {
		t.Fatalf("Deploy was failed")
	}

	t.Run("ServiceTest", service.ServiceCheck(serviceName, releaseName, kubectlOptions, 10))
	t.Run("IngressTest", ingress.IngressCheck(ingressName, releaseName, kubectlOptions, 15))
	// t.Run("SiteTest", helper.Verify(200, siteUrl, "swagger", 5))

}
