package model.Responses;

sealed public interface Response permits SimpleResponse, BulkResponse, ArrayResponse {}

