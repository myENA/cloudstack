# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#   http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.

#Test from the Marvin - Testing in Python wiki

#All tests inherit from cloudstackTestCase
from marvin.cloudstackTestCase import cloudstackTestCase

#Import Integration Libraries

from marvin.codes import FAILED
#base - contains all resources as entities and defines create, delete, list operations on them
from marvin.lib.base import Account, VirtualMachine, ServiceOffering, SimulatorMock, Template, Host

#utils - utility classes for common cleanup, external library wrappers etc
from marvin.lib.utils import cleanup_resources

#common - commonly used methods for all tests are listed here
from marvin.lib.common import get_zone, get_domain, get_template, list_hosts

from nose.plugins.attrib import attr

class TestDeployEfiVm(cloudstackTestCase):
    """Test VM with EFI enabled deploy"""

    def setUp(self):
        self.testdata = self.testClient.getParsedTestDataConfig()
        self.apiclient = self.testClient.getApiClient()

        # Get Zone, Domain and Default Built-in template
        self.domain = get_domain(self.apiclient)
        self.zone = get_zone(self.apiclient, self.testClient.getZoneForTests())
        self.testdata["mode"] = self.zone.networktype
        self.template = get_template(self.apiclient, self.zone.id, self.testdata["ostype"])
        self.hosts = list_hosts(self.apiclient)

        #create a user account
        self.account = Account.create(
            self.apiclient,
            self.testdata["account"],
            domainid=self.domain.id
        )
        #create a service offering
        self.service_offering = ServiceOffering.create(
            self.apiclient,
            self.testdata["service_offerings"]["small"]
        )
        #build cleanup list
        self.cleanup = [
            self.service_offering,
            self.account
        ]

    @attr(tags = ['advanced'], required_hardware="simulator only")
    def test_deploy_efi_vm_failure(self):
        hostWithEfi = False
        templateWithEfi = False

        #check if any host has EFI enabled
        for host in self.hosts:
            if host.capabilities is None:
                continue
            else:
                if "efi" in host.capabilities:
                    hostWithEfi = True
                    break
        #check if the template has EFI details
        if self.template.details is not None:
            if self.template.details["efi"] == "true":
                templateWithEfi = True

        if(hostWithEfi is True and templateWithEfi is True):
            self.skipTest(
                "vm has efi details and there is at least a host with efi enabled so the test will be skipped")
        if(hostWithEfi is False and templateWithEfi is False):
            self.skipTest(
                "vm doesn't have efi details and there is at least a host without efi enabled so the test will be skipped")

        try:
            self.virtual_machine = VirtualMachine.create(
                self.apiclient,
                self.testdata["virtual_machine"],
                zoneid=self.zone.id,
                domainid=self.account.domainid,
                serviceofferingid=self.service_offering.id,
                templateid=self.template.id)
        except:
            list_vms = VirtualMachine.list(self.apiclient)
            self.assertTrue(isinstance(list_vms, list) and len(list_vms) > 0, msg="List VM response empty")
            vm = list_vms[0]
            self.assertEqual(
                vm.state,
                "Error",
                msg="VM is not in Running state")

    def tearDown(self):
        try:
            cleanup_resources(self.apiclient, self.cleanup)
        except Exception as e:
            raise Exception("Warning: Exception during cleanup : %s" % e)


    @attr(tags = ['advanced'], required_hardware="simulator only")
    def test_deploy_efi_vm_succes(self):
        hostWithEfi = False
        templateWithEfi = False

        #check if any host has EFI enabled
        for host in self.hosts:
            if host.capabilities is None:
                continue
            else:
                if "efi" in host.capabilities:
                    hostWithEfi = True
                    break
        #check if the template has EFI details
        if self.template.details is not None:
            if self.template.details["efi"] == "true":
                templateWithEfi = True

        if(templateWithEfi is True and hostWithEfi is False):
            self.skipTest(
                "the vm has efi details and there is no host with efi enabled so this test will be skipped")
        if(templateWithEfi is False and hostWithEfi is True):
            self.skipTest(
                "the vm doesn't have efi details and there is a host with efi enabled. This scenario is not relevant for our case. The test will be skipped")
        if(templateWithEfi is False and hostWithEfi is False):
            self.skipTest(
                "the vm doesn't have efi details and there is no host with efi enabled so this test will be skipped")

        self.virtual_machine = VirtualMachine.create(
            self.apiclient,
            self.testdata["virtual_machine"],
            zoneid=self.zone.id,
            domainid=self.account.domainid,
            serviceofferingid=self.service_offering.id,
            templateid=self.template.id)

        list_vms = VirtualMachine.list(self.apiclient)
        self.assertTrue(isinstance(list_vms, list) and len(list_vms) > 0, msg="List VM response empty")
        vm = list_vms[0]
        self.assertEqual(
            vm.state,
            "Running",
            msg="VM is in Running state")

    def tearDown(self):
        try:
            cleanup_resources(self.apiclient, self.cleanup)
        except Exception as e:
            raise Exception("Warning: Exception during cleanup : %s" % e)
