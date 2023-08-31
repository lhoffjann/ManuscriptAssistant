from lxml import etree
def ValidateXMLWithRNG(rng_file, xml_file):
    relaxng_doc = etree.parse(rng_file)
    relaxng = etree.RelaxNG(relaxng_doc)
    doc = etree.parse(xml_file)
    relaxng.validate(doc)
    log = relaxng.error_log
    print(log)


ValidateXMLWithRNG("/home/lennart/Projects/scan_automatisierung/oxygen-framework/luhmann-datenmodell/common/manuscripts/luhmann_ms.rng", "/mnt/larchiv/manuskripte_scans_fuer_digitalisierung/scans/MS_2857/MS_2857.xml")