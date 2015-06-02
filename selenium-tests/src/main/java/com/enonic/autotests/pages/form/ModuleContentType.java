package com.enonic.autotests.pages.form;


public enum ModuleContentType
{
    DATE( ":date" ), DATE_TIME( ":datetime" ), DOUBLE( ":double" ), TIME( ":time" ), LONG( ":long" ), GEO_POINT( ":geopoint" ), CHECKBOX(
    ":checkbox" ), TEXTLINE1_0( ":textline1_0" ), TEXTLINE1_1( ":textline1_1" ), TEXTLINE0_1( ":textline0_1" ), TEXTLINE2_5(
    ":textline2_5" ), TAG0_5( ":tag0_5" ), TAG2_5( ":tag2_5" ), TAG_UNLIM( ":tag_unlim" ), COMBOBOX0_0( ":combobox0_0" ), COMBOBOX0_1(
    ":combobox0_1" ), COMBOBOX1_1( ":combobox1_1" ), COMBOBOX2_4( ":combobox2_4" ), SINGLE_SELECTOR_COMBOBOX0_1(
    ":ss_combobox0_1" ), SINGLE_SELECTOR_COMBOBOX1_1( ":ss_combobox1_1" ), TEXT_AREA( ":textarea" ), SINGLE_SELECTOR_RADIO0_1(
    ":ss_radio0_1" ), SINGLE_SELECTOR_RADIO1_1( ":ss_radio1_1" ), SINGLE_SELECTOR_DROPDOWN0_1(
    ":ss_dropdown0_1" ), SINGLE_SELECTOR_DROPDOWN1_1( ":ss_dropdown1_1" ), TINY_MCE0_1( ":tiny_mce_0_1" ), TINY_MCE0_2(
    ":tiny_mce_0_2" ), TINY_MCE0_0( ":tiny_mce_0_0" ), IMAGE_SELCTOR0_0( ":imageselector0_0" ), IMAGE_SELCTOR0_1( ":imageselector0_1" );

    private String name;

    public String getName()
    {
        return this.name;
    }

    private ModuleContentType( String name )
    {
        this.name = name;
    }
}
