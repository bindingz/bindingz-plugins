package io.bindingz.contract.plugin.example.sbt

import io.bindingz.sample.latest.InvoiceItemDto

class ClassThatDependsOnCreatedClass {
  var invoiceItem: InvoiceItemDto = new InvoiceItemDto()
  invoiceItem.setOne("One")
}
