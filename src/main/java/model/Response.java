package model;

sealed public interface Response permits SimpleResponse, BulkResponse, ArrayResponse {}

