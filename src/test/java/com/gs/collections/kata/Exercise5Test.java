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

import java.util.List;

import com.gs.collections.api.block.function.Function;
import com.gs.collections.api.block.predicate.Predicate;
import com.gs.collections.api.list.MutableList;
import com.gs.collections.impl.list.mutable.FastList;
import com.gs.collections.impl.utility.ArrayIterate;
import com.gs.collections.impl.utility.Iterate;
import org.junit.Assert;
import org.junit.Test;

public class Exercise5Test extends CompanyDomainForKata
{
    /**
     * Solve this without changing the return type of {@link Company#getSuppliers()}. Find the appropriate method on
     * {@link ArrayIterate}.
     */
    @Test
    public void findSupplierNames()
    {
        Function<Supplier, String> SUPPLIER_NAMES = new Function<Supplier, String>() {
            @Override
            public String valueOf(Supplier object) {
                return object.getName();
            }
        };

        MutableList<String> supplierNames = ArrayIterate.collect(this.company.getSuppliers(), SUPPLIER_NAMES);

        MutableList<String> expectedSupplierNames = FastList.newListWith(
                "Shedtastic",
                "Splendid Crocks",
                "Annoying Pets",
                "Gnomes 'R' Us",
                "Furniture Hamlet",
                "SFD",
                "Doxins");
        Assert.assertEquals(expectedSupplierNames, supplierNames);
    }

    /**
     * Create a {@link Predicate} for Suppliers that supply more than 2 items. Find the number of suppliers that
     * satisfy that Predicate.
     */
    @Test
    public void countSuppliersWithMoreThanTwoItems()
    {
        Predicate<Supplier> moreThanTwoItems = new Predicate<Supplier>() {
            @Override
            public boolean accept(Supplier supplier) {
                return supplier.getItemNames().length > 2;
            }
        };
        int suppliersWithMoreThanTwoItems = ArrayIterate.count(this.company.getSuppliers(), moreThanTwoItems);
        Assert.assertEquals("suppliers with more than 2 items", 5, suppliersWithMoreThanTwoItems);
    }

    /**
     * Try to solve this without changing the return type of {@link Supplier#getItemNames()}.
     */
    @Test
    public void whoSuppliesSandwichToaster()
    {
        // Create a Predicate that will check to see if a Supplier supplies a "sandwich toaster".
        final Predicate<String> hasItem = new Predicate<String>() {
            @Override
            public boolean accept(String each) {
                return each.toLowerCase().contains("sandwich toaster");
            }
        };

        final Predicate<Supplier> suppliesToaster = new Predicate<Supplier>() {
            @Override
            public boolean accept(Supplier each) {
                return ArrayIterate.anySatisfy(each.getItemNames(), hasItem);
            }
        };

        // Find one supplier that supplies toasters.
        Supplier toasterSupplier = ArrayIterate.detect(this.company.getSuppliers(), suppliesToaster);
        Assert.assertNotNull("toaster supplier", toasterSupplier);
        Assert.assertEquals("Doxins", toasterSupplier.getName());
    }

    @Test
    public void filterOrderValues()
    {
        List<Order> orders = this.company.getMostRecentCustomer().getOrders();
        /**
         * Get the order values that are greater than 1.5.
         */
        MutableList<Double> orderValues = FastList.newList(orders).collect(new Function<Order, Double>() {
            @Override
            public Double valueOf(Order object) {
                return object.getValue();
            }
        });
        MutableList<Double> filtered = orderValues.select(new Predicate<Double>() {
            @Override
            public boolean accept(Double each) {
                return each > 1.5;
            }
        });
        Assert.assertEquals(FastList.newListWith(372.5, 1.75), filtered);
    }

    @Test
    public void filterOrders()
    {
        List<Order> orders = this.company.getMostRecentCustomer().getOrders();
        /**
         * Get the actual orders (not their double values) where those orders have a value greater than 2.0.
         */
        MutableList<Order> filtered = FastList.newList(orders).select(new Predicate<Order>() {
            @Override
            public boolean accept(Order each) {
                return each.getValue() > 2.0;
            }
        });
        Assert.assertEquals(FastList.newListWith(Iterate.getFirst(this.company.getMostRecentCustomer().getOrders())), filtered);
    }
}
