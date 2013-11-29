/*
 * Copyright 2011 Goldman Sachs.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gs.collections.kata;

import com.gs.collections.api.block.function.Function;
import com.gs.collections.api.list.MutableList;
import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.test.Verify;
import org.junit.Assert;
import org.junit.Test;
import com.gs.collections.api.block.predicate.Predicate;

public class Exercise1Test extends CompanyDomainForKata
{
    @Test
    public void getCustomerNames()
    {
        Function<Customer, String> nameFunction = new Function<Customer, String>()
        {
            @Override
            public String valueOf(Customer customer)
            {
                return customer.getName();
            }
        };

        /**
         * Get the name of each of the company's customers.
         */
        MutableList<Customer> customers = this.company.getCustomers();
        MutableList<String> customerNames = customers.collect(nameFunction);

        MutableList<String> expectedNames = FastList.newListWith("Fred", "Mary", "Bill");
        Assert.assertEquals(expectedNames, customerNames);
    }

    @Test
    public void getCustomerCities()
    {
        /**
         * Get the city for each of the company's customers. Use an anonymous inner class. Use the IDE to help you as
         * much as possible. Ctrl+space will help you implement an anonymous inner class. Implementing an interface is
         * ctrl+i in IntelliJ. Eclipse's ctrl+1 is auto-fix and works to implement interfaces.
         */
        Function<Customer, String> cityFunction = new Function<Customer, String>() {
            @Override
            public String valueOf(Customer customer) {
                return customer.getCity();
            }
        };

        MutableList<Customer> customers = this.company.getCustomers();
        MutableList<String> customerCities = customers.collect(cityFunction);

        MutableList<String> expectedCities = FastList.newListWith("London", "Liphook", "London");
        Assert.assertEquals(expectedCities, customerCities);
    }

    @Test
    public void getLondonCustomers()
    {
        /**
         * Which customers come from London? Get a collection of those which do. Use an anonymous inner class.
         */
        Function<Customer, Customer> cityFunction = new Function<Customer, Customer>() {
            @Override
            public Customer valueOf(Customer customer) {
                return customer;
            }
        };

        Predicate<Customer> matcher = new Predicate<Customer>() {
            @Override
            public boolean accept(Customer customer) {
                return customer.getCity().equalsIgnoreCase("london");
            }
        };

        MutableList<Customer> customers = this.company.getCustomers();
        MutableList<Customer> customersFromLondon = customers.collectIf(matcher, cityFunction);
        Verify.assertSize("Should be 2 London customers", 2, customersFromLondon);
    }
}
